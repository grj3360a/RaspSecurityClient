package me.security.managers;

import java.util.Arrays;

import me.security.hardware.HardwareElement;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class SecurityManager {
	
	private final NotificationManager notif;
	private final DatabaseManager db;
	private boolean enabled = false;
	
	public SecurityManager(NotificationManager notifications, DatabaseManager db) {
		this.notif = notifications;
		this.db = db;
		
		//initializeHardware();
		this.db.rawLog("Initialized system correctly.\n" + this.notif.toString());
		this.notif.triggerIFTTT("System initialized.");
	}
	
	public void initializeHardware() {
		try {
			//Static initialize elements in HardwareElement.class
			//If not running with WiringPi correctly, this will crash.
			if(HardwareElement.GPIO == null) throw new Exception();
		} catch(UnsatisfiedLinkError ex) {
			ex.printStackTrace();
			System.out.println("Probably not running on RaspberryPi, can't continue.");
			System.exit(-10);
		} catch (Exception gen) {
			gen.printStackTrace();
			System.out.println("Null GPIO, library just crashed.");
			System.exit(-10);
		}
	}
	
	public void alert() {
		
	}
	
}
