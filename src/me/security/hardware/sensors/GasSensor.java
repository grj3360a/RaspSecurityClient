package me.security.hardware.sensors;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 */
public class GasSensor extends Sensor {
	
	@Override
	public SensorType getType() {
		return SensorType.GAS;
	}

}
