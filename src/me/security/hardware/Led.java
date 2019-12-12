package me.security.hardware;

import java.util.concurrent.Callable;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Led {
	
	private final GpioPinDigitalOutput pin;
	
	public Led(SecuManager secuManager, Pin pin) {
		this.pin = secuManager.getGPIO().provisionDigitalOutputPin(pin);
		this.pin.setState(false);
	}
	
	public void flashing() {
		this.pin.blink(600, Long.MAX_VALUE);
	}
	
	public void freezeThenWait() {
		this.pin.pulse(200L, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Led.this.flashing();
				return null;
			}
		});
	}

	public void hide() {
		this.pin.blink(0, 0L);
		this.pin.setState(false);
	}
	
	public void display() {
		this.pin.blink(0, 0L);
		this.pin.setState(true);
	}
	
}
