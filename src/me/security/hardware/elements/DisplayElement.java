package me.security.hardware.elements;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

public class DisplayElement {
	
	private final GpioPinDigitalOutput pin;
	
	public DisplayElement(SecuManager secuManager, Pin pin) {
		this.pin = secuManager.getGPIO().provisionDigitalOutputPin(pin);
		this.pin.setState(false);
	}
	
	public final GpioPinDigitalOutput getPin() {
		return this.pin;
	}

	public void hide() {
		this.pin.blink(0, 0L);
		this.pin.setState(false);
	}
	
	public void display() {
		this.pin.blink(0, 0L);
		this.pin.setState(true);
	}
	
	public void pulse() {
		this.pin.pulse(1000L);
	}
	
	public void blinkIndefinitly() {
		this.getPin().blink(600, Long.MAX_VALUE);
	}
	
}
