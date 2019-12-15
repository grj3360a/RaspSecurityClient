package me.security.hardware.elements;

import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Buzzer extends DisplayElement {
	
	public Buzzer(SecuManager secuManager, Pin pin) {
		super(secuManager, pin);
	}
	
	public void flashingBuzz() {
		this.getPin().blink(800L, 6 * 1000L);
	}
	
}
