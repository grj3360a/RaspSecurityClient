package me.security.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Buzzer {
	
	private final GpioPinDigitalOutput pin;

	public Buzzer(GpioController gpio, Pin pin) {
		this.pin = gpio.provisionDigitalOutputPin(pin);
		this.pin.setState(false);
	}
	
	public void buzz() {
		this.pin.pulse(1000L);
	}
	
	public void flashingBuzz() {
		this.pin.blink(800L, 6 * 1000L);
	}
	
}
