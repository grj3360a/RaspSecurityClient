package me.security.hardware.sensors;

import me.security.hardware.HardwareElement;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public abstract class Sensor extends HardwareElement {
	
	public Sensor() {
		
	}
	
	public abstract SensorType getType();
	
}
