package me.security.hardware.sensors;

import com.google.gson.annotations.Expose;
import com.pi4j.io.gpio.GpioFactory;
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
	
	/**
	 * Create a sensor handler
	 * @param manager The linked security manager
	 * @param name The name of the sensor, or where it is located
	 * @param type The type of the sensor, this will defined it behavior in triggering
	 * @param pin The connected pin
	 */
	public Sensor(SecuManager manager, String name, SensorType type, Pin pin) {
		if(manager == null) throw new IllegalArgumentException();
		if(name == null) throw new IllegalArgumentException();
		if(pin == null) throw new IllegalArgumentException();
		if(type == null) throw new IllegalArgumentException();
		this.manager = manager;
		this.name = name;
		this.pin = GpioFactory.getInstance().provisionDigitalInputPin(pin);
		this.type = type;
		
		this.pin.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getEdge() == PinEdge.FALLING) return;
				Sensor.this.trigger();
			}
		});
	}

	/**
	 * @return The auto incremented id of this sensor
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @see SensorType
	 * @return The type of this sensor
	 */
	public SensorType getType() {
		return type;
	}

	/**
	 * @return True if the sensor is enabled
	 */
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	/**
	 * Enable or disable this sensor
	 */
	public void toggle() {
		this.isEnabled = !this.isEnabled;
	}
	
	/**
	 * Trigger this sensor, this will have an effect only if this sensor is enabled<br>
	 * and the last time it was activated is superior to the activation threshold is passed 
	 * @return Return true if this trigger had an effect on the alarm.
	 */
	public boolean trigger() {
		if(!isEnabled && lastActivated + this.getType().getTimeBetweenActivation() < System.currentTimeMillis()) return false;
		this.lastActivated = System.currentTimeMillis();
		this.manager.triggerAlarm(this.name, this.getType().getAlertMessage());
		return true;
	}

}
