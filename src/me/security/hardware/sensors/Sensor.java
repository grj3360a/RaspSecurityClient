package me.security.hardware.sensors;

import me.security.hardware.HardwareElement;
import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public abstract class Sensor extends HardwareElement {
	
	public static int AUTO_INCREMENT = 0;
	
	public final int id = (AUTO_INCREMENT = AUTO_INCREMENT + 1);
	public boolean isEnabled = false;
	public long lastActivated = -1L;
	public long timeBetweenActivation = 0L;
	public final SecuManager manager;
	
	public Sensor(SecuManager manager) {
		if(manager == null) throw new IllegalArgumentException();
		this.manager = manager;
	}
	
	public abstract SensorType getType();
	public boolean detect() {
		if(!isEnabled && lastActivated + timeBetweenActivation < System.currentTimeMillis()) return false;
		this.lastActivated = System.currentTimeMillis();
		return true;
	}
	
}
