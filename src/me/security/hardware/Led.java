package me.security.hardware;

import java.util.concurrent.Callable;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Led extends HardwareElement {
	
	private final GpioPinDigitalOutput pin;
	
	public Led(GpioPinDigitalOutput pin) {
		this.pin = pin;
		this.pin.setState(false);
		this.waiting();
	}
	
	public void waiting() {
		this.pin.blink(600, Long.MAX_VALUE);
	}
	
	public void freezeThenWait() {
		this.pin.pulse(200L, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Led.this.waiting();
				return null;
			}
		});
	}

	public void hide() {
		this.pin.blink(0, 0L);
		this.pin.setState(false);
	}
	
}
