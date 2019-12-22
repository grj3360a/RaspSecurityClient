package me.security;

import java.io.IOException;
import java.sql.SQLException;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import me.security.notification.NotificationFreeAPI;
import me.security.notification.NotificationIFTTT;
import me.security.windows.SimulatedMode;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class AppClient {
	
	public static boolean SIMULATED_MODE = false;

	public static void main(String[] args) throws IOException, SQLException {
		for(String s : args) {
			if(s.toLowerCase().equals("--simulated")) {
				SimulatedMode.setup();
			}
		}
		
		/*
		 * Database
		 */
		DatabaseManager db = DatabaseManager.generateFromFile();
		
		/*
		 * Notification
		 */
		NotificationManager notif = new NotificationManager();
		
		try {
			NotificationFreeAPI fm = NotificationFreeAPI.generateFromFile();
			notif.add(fm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			NotificationIFTTT ifttt = NotificationIFTTT.generateFromFile();
			notif.add(ifttt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Security handler
		 */
		new SecuManager(notif, db);
		
		//Adding closing mechanism to shutdown DB connection
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	db.close();
		    }
		}));
		
		while(true) { // We need this in main thread to sleep while WiringPi is running in background.
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {}
		}
	}
}
