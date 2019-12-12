package me.security.managers;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import me.security.hardware.Led;
import me.security.hardware.sensors.SensorType;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class SecuManager {
	
	private final NotificationManager notif;
	private final DatabaseManager db;
	private final GpioController GPIO;

	private boolean enabled = false;
	private Led blueLed;
	private Led redLed;
	
	public SecuManager(NotificationManager notifications, DatabaseManager db) throws UnsatisfiedLinkError {
		this.notif = notifications;
		this.db = db;

		this.GPIO = GpioFactory.getInstance();
		initializeHardware();
		
		this.db.rawLog("Initialized system correctly.\n" + this.notif.toString());
		this.notif.triggerIFTTT("System initialized.");
	}
	
	public void initializeHardware() {
		this.blueLed = new Led(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_25));
		this.redLed = new Led(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_22));
	}
	
	public void detect(SensorType type) {
		this.notif.triggerAll("Détection d'un problème : " + type.getAlertMessage());
		redLed.freezeThenWait();
		blueLed.hide();
	}
	
}
