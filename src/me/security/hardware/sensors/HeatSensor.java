package me.security.hardware.sensors;

public class HeatSensor extends Sensor {

	@Override
	public SensorType getType() {
		return SensorType.HEAT;
	}

}
