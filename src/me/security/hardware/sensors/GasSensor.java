package me.security.hardware.sensors;

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
