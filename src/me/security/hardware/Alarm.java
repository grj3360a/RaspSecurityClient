package me.security.hardware;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Alarm {
	
	private final GpioPinDigitalOutput pin;

	public Alarm(SecuManager secuManager, Pin pin) {
		this.pin = secuManager.getGPIO().provisionDigitalOutputPin(pin);
		this.pin.setState(false);
	}
	
	public void flashingAlarm() {
		this.pin.blink(800L, 10 * 1000L);
	}
	
}
