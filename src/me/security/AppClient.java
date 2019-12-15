package me.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import me.security.notification.NotificationFreeAPI;
import me.security.notification.NotificationIFTTT;
import me.security.windows.WindowsMode;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class AppClient {
	
	public static boolean WINDOWS_MODE = false;

	public static void main(String[] args) throws IOException, SQLException {//FIXME Removed throws
		for(String s : args) {
			if(s.toLowerCase().equals("--windows")) {
				WindowsMode.windowsModeSetup();
			}
		}
		
		/*
		 * Database
		 */
		File dbPassword = new File("./database.password");

		if(!dbPassword.exists() || !dbPassword.canRead()) {
			System.out.println("Database password file doesn't exist, impossible to launch.");
			System.exit(-1);
		}
		
		if(Files.readAllLines(dbPassword.toPath()).size() != 4) {
			System.out.println("Database password file doesn't respect defined format, impossible to launch.");
			System.exit(-1);
		}
		
		List<String> dbInfo = Files.readAllLines(dbPassword.toPath());
		DatabaseManager db = new DatabaseManager(dbInfo.get(0), dbInfo.get(1), dbInfo.get(2), dbInfo.get(3));
		
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
		
		while(true) { // We need this thread to sleep while WiringPi is running in background.
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {}
		}
	}
}
