package me.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import me.security.notification.NotificationFreeAPI;
import me.security.notification.NotificationIFTTT;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class AppClient {

	public static void main(String[] args) throws Exception {
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
		
		NotificationFreeAPI fm = generateFree();
		if(fm != null) notif.add(fm);
		
		NotificationIFTTT ifttt = generateIFTTT();
		if(ifttt != null) notif.add(ifttt);
		
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
	}
	
	public static NotificationFreeAPI generateFree() {
		File freePwd = new File("./free.password");

		if(!freePwd.exists() || !freePwd.canRead()) {
			System.out.println("Free password file doesn't exist");
			return null;
		}
		
		List<String> freeInfo;
		try {
			freeInfo = Files.readAllLines(freePwd.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
			
		if(freeInfo.size() != 2) {
			System.out.println("Free password file doesn't respect defined format");
			return null;
		}
		
		try {
			Integer.parseInt(freeInfo.get(0));
		} catch(NumberFormatException e) {
			System.out.println("Free password file have invalid first line");
			return null;
		}
		
		return new NotificationFreeAPI(Integer.parseInt(freeInfo.get(0)), freeInfo.get(1));
	}
	
	public static NotificationIFTTT generateIFTTT() {
		File iftttPwd = new File("./ifttt.password");

		if(!iftttPwd.exists() || !iftttPwd.canRead()) {
			System.out.println("IFTTT password file doesn't exist");
			return null;
		}
		
		List<String> iftttInfo;
		try {
			iftttInfo = Files.readAllLines(iftttPwd.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
			
		if(iftttInfo.size() != 2) {
			System.out.println("IFTTT password file doesn't respect defined format");
			return null;
		}
		
		return new NotificationIFTTT(iftttInfo.get(0), iftttInfo.get(1));
	}

}
