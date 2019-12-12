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

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Digicode {
	
	private static final int BUFFER_SIZE = 16;
	private static final char[][] KEYS = {	{'1', '2', '3', 'A'},
					 	   					{'4', '5', '6', 'B'},
					 	   					{'7', '8', '9', 'C'},
					 	   					{'*', '0', '#', 'D'}};
	
	private List<char[]> codes;
	private char[] typedBuffer = new char[BUFFER_SIZE];

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
	
	
	public Digicode(SecuManager secuManager, GpioController gpio, char[] defaultCode, Pin[] lines, Pin[] columns) {
		if(gpio == null) throw new IllegalArgumentException();
		if(defaultCode == null || defaultCode.length == 0) throw new IllegalArgumentException();
		if(lines == null || lines.length != 4) throw new IllegalArgumentException();
		if(columns == null || columns.length != 4) throw new IllegalArgumentException();
		
		this.codes = new ArrayList<char[]>();
		this.codes.add(defaultCode);
		
		this.padl1 = gpio.provisionDigitalMultipurposePin(lines[0], "0", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		this.padl2 = gpio.provisionDigitalMultipurposePin(lines[1], "1", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		this.padl3 = gpio.provisionDigitalMultipurposePin(lines[2], "2", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		this.padl4 = gpio.provisionDigitalMultipurposePin(lines[3], "3", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		
		this.padc1 = gpio.provisionDigitalMultipurposePin(columns[0], "0", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		this.padc2 = gpio.provisionDigitalMultipurposePin(columns[1], "1", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		this.padc3 = gpio.provisionDigitalMultipurposePin(columns[2], "2", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		this.padc4 = gpio.provisionDigitalMultipurposePin(columns[3], "3", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);

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
							throw new Exception("Multiple input in lines... Unable to calculate key from digicode.");
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
	
	public void input(char c) throws Exception {
		switch(c) {
		
		case '*':// Key to clear all typed letters
			this.typedBuffer = new char[BUFFER_SIZE];
			break;
		
		default:
			throw new IllegalArgumentException("Unknown key pressed, keyboard layout changed ? ");
			
		}
	}
	
	public void onValidate() {
		for(char[] code : this.codes) {
			if(this.typedBuffer == code) {
				//Valid
			}
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
