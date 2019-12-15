package me.security.hardware.elements;

import com.pi4j.io.gpio.Pin;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Alarm extends DisplayElement {
	
	/**
	 * Initialize alarm
	 * @param secuManager The main SecuManager
	 * @param pin The connected pin
	 */
	public Alarm(SecuManager secuManager, Pin pin) {
		super(secuManager, pin);
	}
	
}
