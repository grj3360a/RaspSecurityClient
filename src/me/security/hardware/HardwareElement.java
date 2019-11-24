package me.security.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class HardwareElement {
	
	public static GpioController GPIO = GpioFactory.getInstance();
	
}
