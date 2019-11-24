package me.security.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public abstract class HardwareElement {
	
	public static GpioController GPIO = GpioFactory.getInstance();
	
}
