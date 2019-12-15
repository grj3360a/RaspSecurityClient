package me.security;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.RestAPIManager;
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
	private static SecuManager security;

	public static void main(String[] args) throws Exception {
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
		final DatabaseManager db = new DatabaseManager(dbInfo.get(0), dbInfo.get(1), dbInfo.get(2), dbInfo.get(3));
		
		/*
		 * Notification
		 */
		NotificationManager notif = new NotificationManager();
		
		NotificationFreeAPI fm = NotificationFreeAPI.generateFromFile();
		if(fm != null) notif.add(fm);
		
		NotificationIFTTT ifttt = NotificationIFTTT.generateFromFile();
		if(ifttt != null) notif.add(ifttt);
		
		/*
		 * Security handler
		 */
		security = new SecuManager(notif, db);
		new RestAPIManager(security);
		
		//Adding closing mechanism to shutdown DB connection
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	db.close();
		    }
		}));
		
		while(true) { // We need this thread to sleep while WiringPi is running in background.
			Thread.sleep(1000L);
		}
	}
}
