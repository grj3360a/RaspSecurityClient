package me.security.hardware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import me.security.managers.SecuManager;

/**
 * This class manage a single Digicode
 * 
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Digicode {

	private static final int BUFFER_SIZE = 4;// Maximum number of element of a code
	private static final int MAXIMUM_NUMBER_OF_TRY = 3;
	private static final int WAIT_BEFORE_ACTIVATE = 15 * 1000;// Time between a valid code, and the alarm activation

	public static final char[][] KEYS = { 
			{ '1', '2', '3', 'A' }, 
			{ '4', '5', '6', 'B' }, 
			{ '7', '8', '9', 'C' },
			{ '*', '0', '#', 'D' } };

	private final SecuManager secuManager;

	private List<char[]> passcodes;// Valid codes to enable/disable alarm
	private char[] typedBuffer;// Typed keys buffer
	private char nTypedBuffer;// Number of key already typed

	private long timeLastError;
	private int numberOfError;
	private Thread activatingAlarmThread;// Is the digicode currently waiting to activate the alarm

	private final GpioPinDigitalMultipurpose[] padc;// Columns gpio pins
	private final GpioPinDigitalMultipurpose[] padl;// Lines gpio pins

	/**
	 * Build a digicode from pins and a default code
	 * 
	 * @param secuManager
	 * @param defaultCode
	 * @param lines
	 * @param columns
	 * @throws IllegalArgumentException SecuManager must not be null
	 * @throws IllegalArgumentException There must be 4 lines pins defined
	 * @throws IllegalArgumentException There must be 4 columns pins defined
	 * @throws IllegalArgumentException defaultCode must not be null
	 * @throws IllegalArgumentException defaultCode must not be 4 characters
	 */
	public Digicode(SecuManager secuManager, String defaultCode, Pin[] lines, Pin[] columns)
			throws IllegalArgumentException {
		if (secuManager == null)
			throw new IllegalArgumentException("SecuManager can't be null");
		if (lines == null)
			throw new IllegalArgumentException("Lines can't be null");
		if (lines.length != 4)
			throw new IllegalArgumentException("There must be 4 lines of pins");
		if (columns == null)
			throw new IllegalArgumentException("Columns can't be null");
		if (columns.length != 4)
			throw new IllegalArgumentException("There must be 4 columns of pins");
		if (defaultCode == null)
			throw new IllegalArgumentException("defaultCode can't be null");
		if (defaultCode.length() != 4)
			throw new IllegalArgumentException("defaultCode must be 4 characters");

		this.typedBuffer = new char[BUFFER_SIZE];
		this.nTypedBuffer = 0;

		this.secuManager = secuManager;
		this.passcodes = new ArrayList<char[]>();
		this.addPasscode(defaultCode);

		this.timeLastError = 0;
		this.numberOfError = 0;

		this.padc = new GpioPinDigitalMultipurpose[] { 
				provisionPin(columns, 0), 
				provisionPin(columns, 1),
				provisionPin(columns, 2), 
				provisionPin(columns, 3) };
		this.padl = new GpioPinDigitalMultipurpose[] { 
				provisionPin(lines, 0), 
				provisionPin(lines, 1),
				provisionPin(lines, 2), 
				provisionPin(lines, 3) };

		this.setupLinesColumnsState();

		for (final GpioPinDigitalInput pc : padc) {
			pc.addListener(new GpioPinListenerDigital() {
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
					if (event.getEdge() == PinEdge.FALLING) return;
					if (!pc.isMode(PinMode.DIGITAL_INPUT)) return;

					/*
					 * Reading a column input So now we have to reverse inputs/outputs Column will
					 * now output positive and lines will read
					 */
					for (GpioPinDigitalMultipurpose po : padc) {
						po.setMode(PinMode.DIGITAL_OUTPUT);
						po.setState(true);
					}
					for (GpioPinDigitalMultipurpose po : padl) {
						po.setState(false);
						po.setMode(PinMode.DIGITAL_INPUT);
					}

					//Any high will now be our reading, if multiple input in lines will mean anerror.
					int numberOfInput = 0;
					for (GpioPinDigitalMultipurpose po : padl) {
						if (po.isHigh())
							numberOfInput++;
					}

					try {
						if (numberOfInput > 1) {
							System.err.println("Multiple input in lines (" + pc.getPin().getName()
									+ ")... Unable to calculate key from digicode.");
							return;
						}

						for (GpioPinDigitalMultipurpose po : padl) {
							if (po.isHigh()) {
								input(KEYS[Integer.parseInt(po.getName())][Integer.parseInt(pc.getName())]);
							}
						}
					} catch (NumberFormatException ex) {
						System.err.println("Seems like we having too much columns or errorneous pin names...");
						ex.printStackTrace();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						/*
						 * Reverse inputs/output to initial state.
						 */
						setupLinesColumnsState();
					}

				}
			});
		}
	}

	private void input(char c) throws Exception {
		System.out.println("Digicode : " + c);
		this.secuManager.getBuzzer().shortNote();
		switch (c) {

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			if (this.nTypedBuffer >= BUFFER_SIZE) {
				this.cleanBuffer();
				this.secuManager.getYellowLed().pulse();
				this.secuManager.getBuzzer().multipleLow(1);
				return;
			}

			this.typedBuffer[nTypedBuffer] = c;
			this.nTypedBuffer++;
			break;

		case '#':// Key to validate
			this.onValidate();
			this.cleanBuffer();
			break;

		case 'A':// Unused key for now (Maybe add new code ?)
		case 'B':// Unused key for now
		case 'C':// Unused key for now
		case 'D':// Unused key for now
			break;

		case '*':// Key to clear all typed letters
			this.cleanBuffer();
			break;

		default:
			throw new IllegalArgumentException("Unknown key pressed, keyboard layout changed ? ");

		}
	}

	/**
	 * Provision pin with the Digicode naming scheme
	 * 
	 * @param pins Columns or lines pins
	 * @param i    Pin number in array
	 * @return The multipurpose digital pin
	 */
	private GpioPinDigitalMultipurpose provisionPin(Pin[] pins, int i) {
		return GpioFactory.getInstance().provisionDigitalMultipurposePin(pins[i], i + "", PinMode.DIGITAL_OUTPUT);
	}

	/**
	 * Clear already typed keys on the Digicode
	 */
	private void cleanBuffer() {
		this.nTypedBuffer = 0;
		this.typedBuffer = new char[BUFFER_SIZE];
	}

	public void addPasscode(String code) {
		if (code == null || code.length() != 4)
			throw new IllegalArgumentException("Code is in invalid format (null or lenght different from 4)");
		if (this.passcodes.contains(code.toUpperCase().toCharArray()))
			throw new IllegalArgumentException("Code already added.");
		this.passcodes.add(code.toUpperCase().toCharArray());
	}

	/**
	 * Used when user try to validate buffered keys Toggle alarm if passcode is
	 * valid
	 */
	@SuppressWarnings("deprecation") // We need to stop some thread and this method is deprecated
	private void onValidate() {
		if (this.nTypedBuffer != 4) {
			System.out.println("Digicode used a != 4 passcode.");
			this.secuManager.getBuzzer().multipleHigh(2);
			this.secuManager.getYellowLed().pulse();
			return;
		}

		if (this.numberOfError >= MAXIMUM_NUMBER_OF_TRY && this.timeSinceLastError() < 1000L * 30) {
			this.secuManager.getServerSync().log("Passcode try but too many errors");
			this.secuManager.getBuzzer().multipleHigh(5);
			this.secuManager.getYellowLed().flashing();
			return;
		} else if (this.numberOfError >= MAXIMUM_NUMBER_OF_TRY) {
			this.numberOfError -= 1;
		}

		boolean goodPasscode = false;

		for (char[] code : this.passcodes) {
			if (Arrays.equals(code, this.typedBuffer) && !goodPasscode) {
				if (this.activatingAlarmThread != null && this.activatingAlarmThread.isAlive()) {
					this.activatingAlarmThread.stop();
				}

				this.activatingAlarmThread = new Thread(() -> {
					try {
						if (!secuManager.isEnabled()) {
							secuManager.getBuzzer().success();
							secuManager.getGreenLed().blinkIndefinitly();
							Thread.sleep(WAIT_BEFORE_ACTIVATE);
						} else {
							secuManager.getBuzzer().closing();
						}
					} catch (InterruptedException e) {}
					secuManager.toggleAlarm(new String(code));
				});

				this.activatingAlarmThread.start();

				goodPasscode = true;
				this.timeLastError = 0;
				this.numberOfError = 0;
			}
		}

		if (!goodPasscode) {
			this.timeLastError = System.currentTimeMillis();
			this.numberOfError++;
			this.secuManager.getServerSync().log("Passcode error on Digicode");
			this.secuManager.getServerSync().triggerNotif("Erreur de digicode...");
			this.secuManager.getBuzzer().buzzLowNote();
			this.secuManager.getYellowLed().flashing();
		}

	}

	/**
	 * @return Milliseconds since our last error
	 */
	private long timeSinceLastError() {
		return System.currentTimeMillis() - this.timeLastError;
	}

	/**
	 * Setup lines and columns to initial state<br>
	 * Lines will be high<br>
	 * Columns will be down state<br>
	 * When any column enter high state, this will dispatch a listened event
	 */
	private void setupLinesColumnsState() {
		for (GpioPinDigitalMultipurpose po : padl) {
			po.setDebounce(50);
			po.setMode(PinMode.DIGITAL_OUTPUT);
			po.setPullResistance(PinPullResistance.PULL_DOWN);
			po.setState(true);
		}

		for (GpioPinDigitalMultipurpose po : padc) {
			po.setDebounce(50);
			po.setMode(PinMode.DIGITAL_INPUT);
			po.setPullResistance(PinPullResistance.PULL_DOWN);
		}
	}

	public boolean isActivating() {
		return this.activatingAlarmThread != null && this.activatingAlarmThread.isAlive();
	}

}
