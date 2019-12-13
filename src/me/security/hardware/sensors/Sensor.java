package me.security.hardware.sensors;

import com.google.gson.annotations.Expose;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
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
	
	
	@Expose private final int id = (AUTO_INCREMENT = AUTO_INCREMENT + 1);
	@Expose private boolean isEnabled = false;
	@Expose private long lastActivated = -1L;
			private final SecuManager manager;
	@Expose private final String name;
	@Expose private final SensorType type;
			private GpioPinDigitalInput pin;
	
	public Sensor(SecuManager manager, String name, SensorType type, Pin pin) {
		if(manager == null) throw new IllegalArgumentException();
		if(name == null) throw new IllegalArgumentException();
		if(pin == null) throw new IllegalArgumentException();
		if(type == null) throw new IllegalArgumentException();
		this.manager = manager;
		this.name = name;
		this.pin = manager.getGPIO().provisionDigitalInputPin(pin);
		this.type = type;
		
		this.pin.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getEdge() == PinEdge.FALLING) return;
				Sensor.this.detect();
			}
		});
	}
	
	public void toggle() {
		this.isEnabled = !this.isEnabled;
	}
	
	public SensorType getType() {
		return type;
	}
	
	public boolean detect() {
		if(!isEnabled && lastActivated + this.getType().getTimeBetweenActivation() < System.currentTimeMillis()) return false;
		this.lastActivated = System.currentTimeMillis();
		this.manager.triggerAlarm(this.name, this.getType());
		return true;
	}
	
}
