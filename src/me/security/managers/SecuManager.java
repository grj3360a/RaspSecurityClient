package me.security.managers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
public class SecuManager implements Closeable {

	public static final Pin[] digiLines = new Pin[] { RaspiPin.GPIO_26, RaspiPin.GPIO_23, RaspiPin.GPIO_22,
			RaspiPin.GPIO_21 };
	public static final Pin[] digiColumns = new Pin[] { RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_01,
			RaspiPin.GPIO_00 };

	private final NotificationManager notif;
	private final DatabaseManager db;
	private RestAPIManager restApi;

	private boolean enabled = false;
	private boolean alarmTriggered = false;
	private File saveFile;

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
	 * This will initialize all the hardware needed and log an initialization
	 * message<br>
	 * This will also run a RestAPIManager instance
	 * 
	 * @param notifications
	 * @param db
	 * @throws UnsatisfiedLinkError If this doesn't run with required C libraries
	 * @throws IOException          RestAPI input output
	 */
	public SecuManager(NotificationManager notifications, DatabaseManager db) throws UnsatisfiedLinkError, IOException {
		if (notifications == null)
			throw new IllegalArgumentException("NotificationManager cannot be null");
		if (db == null)
			throw new IllegalArgumentException("DatabaseManager cannot be null");
		this.notif = notifications;
		this.db = db;

		this.saveFile = new File("alarm.dat");

		this.blueLed = new DisplayElement(RaspiPin.GPIO_25);
		this.blueLed.blinkIndefinitly();
		this.redLed = new DisplayElement(RaspiPin.GPIO_24);
		this.yellowLed = new DisplayElement(RaspiPin.GPIO_27);
		this.greenLed = new DisplayElement(RaspiPin.GPIO_28);

		this.alarm = new DisplayElement(RaspiPin.GPIO_16);
		this.buzzer = new Buzzer(RaspiPin.GPIO_15);

		this.digicode = new Digicode(this, "1574", digiLines, digiColumns);

		this.sensors = new ArrayList<Sensor>();
		this.sensors.add(new Sensor(this, "Mouvement salon", SensorType.MOTION, RaspiPin.GPIO_04));
		this.sensors.add(new Sensor(this, "Fenêtre avant", SensorType.OPEN, RaspiPin.GPIO_07));
		
		// this.sensors.add(new Sensor(this, "Fenêtre arrière", SensorType.OPEN, RaspiPin.GPIO_31));
		// this.sensors.add(new Sensor(this, "Chaleur salon", SensorType.HEAT, RaspiPin.GPIO_28));
		// this.sensors.add(new Sensor(this, "Gaz salon", SensorType.GAS, RaspiPin.GPIO_24));
		
		for (Sensor s : this.sensors) {
			s.toggle();// TODO Remove and from save
		}

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
		this.redLed.set(true);
		this.alarmTriggered = true;
	}

	public void toggleAlarm(String code) {
		if (this.enabled && this.alarmTriggered) {
			this.alarm.set(false);
			this.redLed.set(false);
			this.db.log("Triggered alarm is now controlled by " + code);
			this.notif.triggerFree("Alarme désactivée après une détection");
		}
		this.enabled = !enabled;
		this.saveAlarmState();
		this.db.log("Alarm toggled " + (enabled ? "ON" : "OFF") + " with code : " + code);
		this.notif.triggerIFTTT("Alarme " + (enabled ? "activée" : "désactivée") + " avec le code " + code);
		this.buzzer.buzzHighNote();
		this.greenLed.set(enabled);
	}

	public void saveAlarmState() {
		try {
			FileUtils.write(this.saveFile, this.isEnabled() + "", StandardCharsets.UTF_8);
		} catch (IOException e) { // Saving alarm must not fail, otherwise can't do anything about it.
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		this.saveAlarmState();
		this.buzzer.buzzHighNote();
		this.alarm.set(false);
		this.blueLed.set(false);
		this.redLed.set(false);
		this.yellowLed.set(false);
		this.greenLed.set(false);
		this.db.log("System shutdown...");
		this.db.close();
	}

	/**
	 * Doesn't allow sensor list modification
	 * 
	 * @return The list of sensors
	 */
	public final List<Sensor> getSensors() {
		return new ArrayList<Sensor>(this.sensors);
	}

	public Buzzer getBuzzer() {
		return this.buzzer;
	}

	public DisplayElement getRedLed() {
		return this.redLed;
	}

	public DisplayElement getYellowLed() {
		return this.yellowLed;
	}

	public DisplayElement getGreenLed() {
		return this.greenLed;
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
