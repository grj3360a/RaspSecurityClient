package me.security.hardware;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Buzzer {
	
	private final GpioPinDigitalOutput pin;

	public Buzzer(SecuManager secuManager, Pin pin) {
		this.pin = secuManager.getGPIO().provisionDigitalOutputPin(pin);
		this.pin.setState(false);
	}
	
	public void buzz() {
		this.pin.pulse(1000L);
	}
	
	public void flashingBuzz() {
		this.pin.blink(800L, 6 * 1000L);
	}
	
}
