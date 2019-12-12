package me.security.managers;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import me.security.hardware.Alarm;
import me.security.hardware.Buzzer;
import me.security.hardware.Digicode;
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
	private boolean alarmTriggered = false;
	private Led blueLed;
	private Led redLed;

	private Alarm alarm;
	private Buzzer buzzer;
	
	private Digicode digicode;
	
	public SecuManager(NotificationManager notifications, DatabaseManager db) throws UnsatisfiedLinkError {
		this.notif = notifications;
		this.db = db;

		this.GPIO = GpioFactory.getInstance();
		initializeHardware();
		
		this.db.rawLog("Initialized system correctly.\n" + this.notif.toString());
		this.notif.triggerIFTTT("System initialized.");
	}
	
	public void initializeHardware() {
		this.blueLed = new Led(GPIO, RaspiPin.GPIO_25);
		this.blueLed.flashing();
		this.redLed = new Led(GPIO, RaspiPin.GPIO_22);
		this.redLed.hide();

		this.alarm = new Alarm(GPIO, RaspiPin.GPIO_00);
		this.buzzer = new Buzzer(GPIO, RaspiPin.GPIO_26);
		
		this.digicode = new Digicode
				(this,
				GPIO, 
				"1574", 
				new Pin[]{RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[]{RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
	}
	
	public void triggerAlarm(String sensorName, SensorType type) {
		if (!enabled) {
			System.out.println("Detection with disabled system. (" + sensorName + " | " + type.getAlertMessage() + ")");
			return;
		}
		
		this.notif.triggerAll("Détection d'un problème à " + sensorName + " : " + type.getAlertMessage());
		this.db.alert(sensorName, type);
		this.alarm.flashingAlarm();
		this.redLed.freezeThenWait();
		this.blueLed.hide();
		this.alarmTriggered = true;
	}

	public void toggleAlarm(String code) {
		if(this.enabled && this.alarmTriggered) {
			this.blueLed.flashing();
			this.redLed.hide();
			this.db.rawLog("Triggered alarm is now controlled by " + code);
			this.notif.triggerFree("Alarme désactivée après une détection");
		}
		this.enabled = !enabled;
		this.db.rawLog("Alarm toggled " + (enabled ? "ON" : "OFF") + " with code : " + code);
		this.notif.triggerIFTTT("Alarme " + (enabled ? "activée" : "désactivée") + " avec le code " + code);
	}
	
}
