package me.security.hardware.sensors;

public class MotionSensor extends Sensor {

	@Override
	public SensorType getType() {
		return SensorType.MOTION;
	}

}
