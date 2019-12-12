package me.security.hardware;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Buzzer {
	
	private final GpioPinDigitalOutput pin;
	
	public Buzzer(GpioPinDigitalOutput pin) {
		this.pin = pin;
		this.pin.setState(false);
	}
	
	public void buzz() {
		this.pin.pulse(2000L);
	}
	
}
