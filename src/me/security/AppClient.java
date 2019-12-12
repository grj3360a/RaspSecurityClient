package me.security;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.hardware.Digicode;
import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.RestAPIManager;
import me.security.managers.SecuManager;
import me.security.notification.NotificationFreeAPI;
import me.security.notification.NotificationIFTTT;

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
				windowsModeSetup();
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
		
		NotificationFreeAPI fm = generateFree();
		if(fm != null) notif.add(fm);
		
		NotificationIFTTT ifttt = generateIFTTT();
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
	
	public static void windowsModeSetup() {
		WINDOWS_MODE = true;
        GpioProvider provider = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(provider);
        System.out.println("Enabled system in windows mode, simulated environment.");
        
        //Activating simulation.
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000L);//Just Waiting for system to be setup.
					
					Field f = SecuManager.class.getDeclaredField("digicode");
					f.setAccessible(true);
					Digicode d = (Digicode) f.get(security);
					
					Method m = Digicode.class.getDeclaredMethod("input", char.class);
					m.setAccessible(true);
					
					m.invoke(d, '1');
					m.invoke(d, '5');
					m.invoke(d, '7');
					m.invoke(d, '4');
					m.invoke(d, '#');
					
				} catch (Exception e) {//We don't even care
					e.printStackTrace();
				}
			}
		}).start();;
	}

}
