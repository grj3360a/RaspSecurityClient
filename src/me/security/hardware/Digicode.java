package me.security.hardware;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import me.security.hardware.sensors.SensorType;
import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Digicode {
	
	private static final int BUFFER_SIZE = 4;
	private static final int MAXIMUM_NUMBER_OF_TRY = 3;
	private static final char[][] KEYS = {	{'1', '2', '3', 'A'},
					 	   					{'4', '5', '6', 'B'},
					 	   					{'7', '8', '9', 'C'},
					 	   					{'*', '0', '#', 'D'}};

	private final SecuManager secuManager;
	
	private List<char[]> passcodes;//Valid codes to enable/disable alarm
	private char[] typedBuffer = new char[BUFFER_SIZE];//Typed keys buffer
	private char nTypedBuffer = 0;//Number of key already typed
	
	private long timeLastError = 0;
	private int numberOfError = 0;

	private GpioPinDigitalMultipurpose padl1;
	private GpioPinDigitalMultipurpose padl2;
	private GpioPinDigitalMultipurpose padl3;
	private GpioPinDigitalMultipurpose padl4;
	
	private GpioPinDigitalMultipurpose padc1;
	private GpioPinDigitalMultipurpose padc2;
	private GpioPinDigitalMultipurpose padc3;
	private GpioPinDigitalMultipurpose padc4;

	private final GpioPinDigitalMultipurpose[] pad;
	private final GpioPinDigitalMultipurpose[] padc;
	private final GpioPinDigitalMultipurpose[] padl;
	
	
	public Digicode(SecuManager secuManager, String defaultCode, Pin[] lines, Pin[] columns) {
		if(secuManager == null) throw new IllegalArgumentException();
		if(lines == null || lines.length != 4) throw new IllegalArgumentException();
		if(columns == null || columns.length != 4) throw new IllegalArgumentException();
		
		this.secuManager = secuManager;
		this.passcodes = new ArrayList<char[]>();
		this.addPasscode(defaultCode);
		
		this.padl1 = secuManager.getGPIO().provisionDigitalMultipurposePin(lines[0], "0", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		this.padl2 = secuManager.getGPIO().provisionDigitalMultipurposePin(lines[1], "1", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		this.padl3 = secuManager.getGPIO().provisionDigitalMultipurposePin(lines[2], "2", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		this.padl4 = secuManager.getGPIO().provisionDigitalMultipurposePin(lines[3], "3", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		
		this.padc1 = secuManager.getGPIO().provisionDigitalMultipurposePin(columns[0], "0", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		this.padc2 = secuManager.getGPIO().provisionDigitalMultipurposePin(columns[1], "1", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		this.padc3 = secuManager.getGPIO().provisionDigitalMultipurposePin(columns[2], "2", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		this.padc4 = secuManager.getGPIO().provisionDigitalMultipurposePin(columns[3], "3", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);

		this.pad = new GpioPinDigitalMultipurpose[]{padc1, padc2, padc3, padc4, padl1, padl2, padl3, padl4};
		this.padc = new GpioPinDigitalMultipurpose[]{padc1, padc2, padc3, padc4};
		this.padl = new GpioPinDigitalMultipurpose[]{padl1, padl2, padl3, padl4};
		
		for(GpioPinDigitalMultipurpose g : this.pad) {
			g.setDebounce(50);
		}
		
		this.setupLinesColumnsState();
		
		for(final GpioPinDigitalInput pc : padc) {
			pc.addListener(new GpioPinListenerDigital() {
				
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
					if(event.getEdge() == PinEdge.FALLING) return;
					if(!pc.isMode(PinMode.DIGITAL_INPUT)) return;
					
					/*
					 * Reading a column input
					 * So now we have to reverse inputs/outputs
					 * Column will now output positive and lines will read
					 */
					
					for(GpioPinDigitalMultipurpose po : padc) {
						po.setMode(PinMode.DIGITAL_OUTPUT);
						po.setState(true);
					}
					for(GpioPinDigitalMultipurpose po : padl) {
						po.setState(false);
						po.setMode(PinMode.DIGITAL_INPUT);
					}
					
					/*
					 * Any high will now be our reading,
					 * if multiple input in lines will mean an error.
					 */
					
					int numberOfInput = 0;
					for(GpioPinDigitalMultipurpose po : padl) {
						if(po.isHigh()) numberOfInput++;
					}
					
					try {
						if(numberOfInput > 1 || numberOfInput == 0) {
							throw new Exception((numberOfInput > 0 ? "Multiple" : "No") + " input in lines... Unable to calculate key from digicode.");
						}
						
						for(GpioPinDigitalMultipurpose po : padl) {
							if(po.isHigh()) {
								input(KEYS[Integer.parseInt(po.getName())][Integer.parseInt(pc.getName())]);
							}
						}
					} catch(NumberFormatException ex) {
						System.err.println("Seems like we having too much columns or errorneous pin names...");
						ex.printStackTrace();
					} catch(Exception ex) {
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
		System.out.println("Just pressed on digicode : " + c);
		switch(c) {

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
			if(this.nTypedBuffer >= BUFFER_SIZE) {
				//FIXME Too much keys pressed.
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
	
	private void cleanBuffer() {
		System.out.println("Cleared digicode buffered keys.");
		this.typedBuffer = new char[BUFFER_SIZE];
	}
	
	private void addPasscode(String code) {
		if(code == null || code.length() != 4) throw new IllegalArgumentException("Code is in invalid format (null or lenght different from 4)");
		if(this.passcodes.contains(code.toUpperCase().toCharArray())) throw new IllegalArgumentException("Code already added.");
		this.passcodes.add(code.toUpperCase().toCharArray());
	}
	
	/**
	 * Used when user try to validate buffered keys
	 * Toggle alarm if passcode is valid
	 */
	private void onValidate() {
		if(this.nTypedBuffer != 4) {
			throw new IllegalStateException("Digicode used a != 4 passcode.");
		}
		
		System.out.println("Validating passcode.");
		
		boolean goodPasscode = false;
		
		for(char[] code : this.passcodes) {
			if(this.typedBuffer == code && !goodPasscode) {
				this.secuManager.toggleAlarm(new String(code));
				goodPasscode = true;
				this.timeLastError = 0;
				this.numberOfError = 0;
			}
		}
		
		if(!goodPasscode) {
			this.timeLastError = System.currentTimeMillis();
			this.numberOfError++;
			this.secuManager.getDb().log("Passcode error on Digicode");
			this.secuManager.getNotif().triggerIFTTT("Erreur de digicode...");
			this.secuManager.getBuzzer().buzz();
		}
		
		if(this.numberOfError >= MAXIMUM_NUMBER_OF_TRY) {
			this.secuManager.triggerAlarm("Nombre d'erreur", SensorType.DIGICODE);
		}
	}

	private void setupLinesColumnsState() {
		for(GpioPinDigitalMultipurpose po : padl) {
			po.setMode(PinMode.DIGITAL_OUTPUT);
			po.setState(true);
		}
		
		for(GpioPinDigitalMultipurpose po : padc) {
			po.setState(false);
			po.setMode(PinMode.DIGITAL_INPUT);
		}
	}
	
}
