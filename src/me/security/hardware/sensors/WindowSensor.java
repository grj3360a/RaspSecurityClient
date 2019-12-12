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
public class WindowSensor extends Sensor {

	public WindowSensor(SecuManager manager, GpioPinDigitalInput pin) {
		super(manager);
		if(pin == null) throw new IllegalArgumentException();
		
		pin.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getEdge() == PinEdge.FALLING) return;
				WindowSensor.this.detect();
			}
		});
	}
	
	public boolean detect() {
		if(super.detect()) {
			this.manager.detect(this.getType());
			return true;
		}
		return false;
	}
	
	@Override
	public SensorType getType() {
		return SensorType.OPEN;
	}

}
