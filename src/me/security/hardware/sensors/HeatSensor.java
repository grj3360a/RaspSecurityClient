package me.security.hardware.sensors;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 */
public class HeatSensor extends Sensor {

	@Override
	public SensorType getType() {
		return SensorType.HEAT;
	}

}
