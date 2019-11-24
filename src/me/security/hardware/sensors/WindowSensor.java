package me.security.hardware.sensors;

public class WindowSensor extends Sensor {

	@Override
	public SensorType getType() {
		return SensorType.OPEN;
	}

}
