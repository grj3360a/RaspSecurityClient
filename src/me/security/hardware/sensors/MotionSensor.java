package me.security.hardware.sensors;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 */
public class MotionSensor extends Sensor {

	@Override
	public SensorType getType() {
		return SensorType.MOTION;
	}

}
