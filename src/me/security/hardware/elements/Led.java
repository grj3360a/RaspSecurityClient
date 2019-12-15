package me.security.hardware.elements;

import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Led extends DisplayElement {
	
	public Led(SecuManager secuManager, Pin pin) {
		super(secuManager, pin);
	}
	
	public void freezeThenWait() {
		this.getPin().pulse(2000L);
	}

}
