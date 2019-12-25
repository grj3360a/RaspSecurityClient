package me.security.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import me.security.hardware.Buzzer;
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
	private RestAPIManager restApi;

	private boolean enabled = false;
	private boolean alarmTriggered = false;
	
	private List<Sensor> sensors;
	private DisplayElement blueLed;
	private DisplayElement redLed;
	private DisplayElement yellowLed;
	private DisplayElement greenLed;

	private DisplayElement alarm;
	private Buzzer buzzer;
	
	private Digicode digicode;
	
	/**
	 * Create an instance of a Security Manager<br>
	 * This will initialize all the hardware needed and log an initialization message<br>
	 * This will also run a RestAPIManager instance
	 * @param notifications
	 * @param db
	 * @throws UnsatisfiedLinkError
	 * @throws IOException
	 */
	public SecuManager(NotificationManager notifications, DatabaseManager db) throws UnsatisfiedLinkError, IOException {
		if(notifications == null) throw new IllegalArgumentException("NotificationManager cannot be null");
		if(db == null) throw new IllegalArgumentException("DatabaseManager cannot be null");
		this.notif = notifications;
		this.db = db;

		this.blueLed = new DisplayElement(RaspiPin.GPIO_25);
		this.blueLed.blinkIndefinitly();
		this.redLed = new DisplayElement(RaspiPin.GPIO_24);
		this.redLed.off();
		this.yellowLed = new DisplayElement(RaspiPin.GPIO_27);
		this.yellowLed.off();
		this.greenLed = new DisplayElement(RaspiPin.GPIO_28);
		this.greenLed.off();

		this.alarm = new DisplayElement(RaspiPin.GPIO_16);
		this.buzzer = new Buzzer(RaspiPin.GPIO_15);
		
		this.digicode = new Digicode
				(this,
				"1574", 
				new Pin[]{RaspiPin.GPIO_26, RaspiPin.GPIO_23, RaspiPin.GPIO_22, RaspiPin.GPIO_21},
				new Pin[]{RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_01, RaspiPin.GPIO_00});
		
		this.sensors = new ArrayList<Sensor>();
		this.sensors.add(new Sensor(this, "Mouvement salon", SensorType.MOTION, RaspiPin.GPIO_04));
		this.sensors.add(new Sensor(this, "Fenêtre avant", SensorType.OPEN, RaspiPin.GPIO_07));
		//this.sensors.add(new Sensor(this, "Fenêtre arrière", SensorType.OPEN, RaspiPin.GPIO_31));
		//this.sensors.add(new Sensor(this, "Chaleur salon", SensorType.HEAT, RaspiPin.GPIO_28));
		//this.sensors.add(new Sensor(this, "Gaz salon", SensorType.GAS, RaspiPin.GPIO_24));
		
		this.db.log("Initialized system correctly.\n" + this.notif.toString());
		this.notif.triggerIFTTT("System initialized.");

		this.restApi = new RestAPIManager(this);
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
		this.buzzer.buzzHighNote();
	}

	/**
	 * Doesn't allow sensor list modification
	 * @return The list of sensors
	 */
	public final List<Sensor> getSensors() {
		return new ArrayList<Sensor>(this.sensors);
	}

	public Buzzer getBuzzer() {
		return this.buzzer;
	}

	public DisplayElement getBlueLed() {
		return blueLed;
	}

	public DisplayElement getRedLed() {
		return redLed;
	}

	public DisplayElement getYellowLed() {
		return yellowLed;
	}

	public DisplayElement getGreenLed() {
		return greenLed;
	}

	public Digicode getDigicode() {
		return this.digicode;
	}
	
	public RestAPIManager getRestApi() {
		return this.restApi;
	}
	
	public NotificationManager getNotif() {
		return this.notif;
	}

	public DatabaseManager getDb() {
		return this.db;
	}

	public boolean hasAlarmTriggered() {
		return this.alarmTriggered;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

}
