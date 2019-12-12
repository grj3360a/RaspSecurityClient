package me.security.hardware.sensors;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import me.security.managers.SecuManager;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Sensor {
	
	public static int AUTO_INCREMENT = 0;
	
	public final int id = (AUTO_INCREMENT = AUTO_INCREMENT + 1);
	public boolean isEnabled = false;
	public long lastActivated = -1L;
	public final SecuManager manager;
	public final String name;
	public final SensorType type;
	@SuppressWarnings("unused")//TODO Remove ?
	private GpioPinDigitalInput pin;
	
	public Sensor(SecuManager manager, String name, SensorType type, GpioPinDigitalInput pin) {
		if(manager == null) throw new IllegalArgumentException();
		if(name == null) throw new IllegalArgumentException();
		if(pin == null) throw new IllegalArgumentException();
		if(type == null) throw new IllegalArgumentException();
		this.manager = manager;
		this.name = name;
		this.pin = pin;
		this.type = type;
		
		pin.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getEdge() == PinEdge.FALLING) return;
				Sensor.this.detect();
			}
		});
	}
	
	public SensorType getType() {
		return type;
	}
	
	public boolean detect() {
		if(!isEnabled && lastActivated + this.getType().getTimeBetweenActivation() < System.currentTimeMillis()) return false;
		this.lastActivated = System.currentTimeMillis();
		this.manager.detect(this.name, this.getType());
		return true;
	}
	
}
