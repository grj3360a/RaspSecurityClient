package me.security.managers;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import me.security.hardware.Digicode;
import me.security.hardware.DisplayElement;
import me.security.hardware.sensors.Sensor;
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
	
	private List<Sensor> sensors;
	private DisplayElement blueLed;
	private DisplayElement redLed;

	private DisplayElement alarm;
	private DisplayElement buzzer;
	
	private Digicode digicode;
	
	public SecuManager(NotificationManager notifications, DatabaseManager db) throws UnsatisfiedLinkError {
		if(notifications == null) throw new IllegalArgumentException();
		if(db == null) throw new IllegalArgumentException();
		this.notif = notifications;
		this.db = db;

		this.GPIO = GpioFactory.getInstance();
		initializeHardware();
		
		this.db.log("Initialized system correctly.\n" + this.notif.toString());
		this.notif.triggerIFTTT("System initialized.");
	}
	
	public void initializeHardware() {
		this.blueLed = new DisplayElement(this, RaspiPin.GPIO_25);
		this.blueLed.blinkIndefinitly();
		this.redLed = new DisplayElement(this, RaspiPin.GPIO_22);
		this.redLed.off();

		this.alarm = new DisplayElement(this, RaspiPin.GPIO_27);
		this.buzzer = new DisplayElement(this, RaspiPin.GPIO_26);
		
		this.digicode = new Digicode
				(this,
				"1574", 
				new Pin[]{RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[]{RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
		
		this.sensors = new ArrayList<Sensor>();
		this.sensors.add(new Sensor(this, "Mouvement salon", SensorType.MOTION, RaspiPin.GPIO_29));
		this.sensors.add(new Sensor(this, "Fenêtre avant", SensorType.OPEN, RaspiPin.GPIO_30));
		this.sensors.add(new Sensor(this, "Fenêtre arrière", SensorType.OPEN, RaspiPin.GPIO_31));
		this.sensors.add(new Sensor(this, "Chaleur salon", SensorType.HEAT, RaspiPin.GPIO_28));
		this.sensors.add(new Sensor(this, "Gaz salon", SensorType.GAS, RaspiPin.GPIO_24));
	}
	
	public void triggerAlarm(String sensorName, String alertMessage) {
		if (!enabled) {
			System.out.println("Detection with disabled system. (" + sensorName + " | " + alertMessage + ")");
			return;
		}
		
		this.notif.triggerAll("Détection d'un problème à " + sensorName + " : " + alertMessage);
		this.db.alert(sensorName, alertMessage);
		this.alarm.blinkIndefinitly();
		this.redLed.on();
		this.blueLed.off();
		this.alarmTriggered = true;
	}

	public void toggleAlarm(String code) {
		if(this.enabled && this.alarmTriggered) {
			this.alarm.off();
			this.blueLed.blinkIndefinitly();
			this.redLed.off();
			this.db.log("Triggered alarm is now controlled by " + code);
			this.notif.triggerFree("Alarme désactivée après une détection");
		}
		this.enabled = !enabled;
		this.db.log("Alarm toggled " + (enabled ? "ON" : "OFF") + " with code : " + code);
		this.notif.triggerIFTTT("Alarme " + (enabled ? "activée" : "désactivée") + " avec le code " + code);
		this.buzzer.flashingBuzz();
	}
	
	public GpioController getGPIO() {
		return GPIO;
	}

	public NotificationManager getNotif() {
		return notif;
	}

	public DatabaseManager getDb() {
		return db;
	}

	public DisplayElement getBuzzer() {
		return buzzer;
	}

	/**
	 * Doesn't allow sensor list modification
	 * @return The list of sensors
	 */
	public final List<Sensor> getSensors() {
		return new ArrayList<Sensor>(this.sensors);
	}

	public boolean isEnabled() {
		return enabled;
	}

}
