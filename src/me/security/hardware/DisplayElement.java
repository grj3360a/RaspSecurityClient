package me.security.hardware;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

/**
 * A hardware element to display an information
 * This can be a led, alarm or a buzzer for example
 * @author Geraldes Jocelyn
 * @since 24/12/2019
 */
public class DisplayElement {
	
	private final GpioPinDigitalOutput pin;
	
	/**
	 * Create an instance of a display element, that can be a led, alarm or a buzzer
	 * @param pin The pin linked to that element
	 */
	public DisplayElement(Pin pin) {
		this.pin = GpioFactory.getInstance().provisionDigitalOutputPin(pin, "Display Element");
		this.pin.setState(false);
	}
	
	/**
	 * Turn off the element, also disable blinking
	 */
	public void off() {
		this.pin.blink(0, 0L);
		this.pin.setState(false);
	}
	
	/**
	 * Turn on the element, also disable current blinking
	 */
	public void on() {
		this.pin.blink(0, 0L);
		this.pin.setState(true);
	}
	
	/**
	 * Pulse the element ON for one second
	 */
	public void pulse() {
		this.pin.pulse(1000L);
	}
	
	/**
	 * Blink the element every .6s for an infinite amount of time
	 */
	public void blinkIndefinitly() {
		this.pin.blink(600, Long.MAX_VALUE);
	}
	
	/**
	 * Blink the element every .8s for 6 seconds
	 */
	public void flashing() {
		this.pin.blink(800L, 6 * 1000L);
	}
	
}
