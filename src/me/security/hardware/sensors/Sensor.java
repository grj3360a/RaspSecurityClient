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

	public static final long WAIT_BEFORE_TRIGGER = 15 * 1000L;
	public static int AUTO_INCREMENT = 0;

	@Expose private final int id;
	@Expose private boolean isEnabled;
	@Expose private long lastActivated;
	@Expose private final String name;
	@Expose private final SensorType type;

	private final SecuManager secuManager;
	private GpioPinDigitalInput pin;

	private Thread triggering;

	/**
	 * Create a sensor handler
	 * 
	 * @param manager The linked security manager
	 * @param name    The name of the sensor, or where it is located
	 * @param type    The type of the sensor, this will defined it behavior in triggering
	 * @param pin     The connected pin
	 * @throws IllegalArgumentException All elements must be different from null
	 */
	public Sensor(SecuManager manager, String name, SensorType type, Pin pin) {
		if (manager == null) throw new IllegalArgumentException("SecuManager cannot be null");
		if (name == null) throw new IllegalArgumentException("Sensor must have a name");
		if (type == null) throw new IllegalArgumentException("Sensor must have a type");
		if (pin == null) throw new IllegalArgumentException("Sensor must be connected to a pin");
		this.id = (AUTO_INCREMENT = AUTO_INCREMENT + 1);
		this.isEnabled = false;
		this.lastActivated = -1L;
		this.secuManager = manager;
		this.name = name;
		this.pin = GpioFactory.getInstance().provisionDigitalInputPin(pin, "Sensor " + type + " " + name);
		this.type = type;

		this.pin.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getEdge() == PinEdge.FALLING) return;
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
	 * @return Is currently triggering alarm (counting down 'WAIT_BEFORE_TRIGGER')
	 */
	public boolean isTriggering() {
		return this.triggering != null && this.triggering.isAlive();
	}

	/**
	 * Trigger this sensor, this will have an effect only if this sensor is enabled<br>
	 * and the last time it was activated is superior to the activation threshold<br><br>
	 * This will wait 'WAIT_BEFORE_ALARM' time before triggering the main alarm
	 * 
	 * @see SensorType.getTimeBetweenActivation()
	 * @return Return true if this trigger had an effect on the alarm.
	 */
	@SuppressWarnings("deprecation")
	public void trigger() {
		System.out.println("Detection on sensor : " + this.name);
		if (!isEnabled && lastActivated + this.getType().getTimeBetweenActivation() < System.currentTimeMillis())
			return;
		this.lastActivated = System.currentTimeMillis();

		if (this.triggering != null)
			this.triggering.stop();

		this.triggering = new Thread(() -> {
			this.secuManager.getRedLed().pulse();
			if(!this.secuManager.isEnabled()) return;

			try {
				this.secuManager.getRedLed().blinkIndefinitly();
				Thread.sleep(WAIT_BEFORE_TRIGGER);
			} catch (InterruptedException e) {}

			if (!isEnabled)
				return;// Still enabled ?
			this.secuManager.triggerAlarm(this.name, this.getType().getAlertMessage());
		});
		this.triggering.start();
	}

}
