package me.security.managers;

public class SecurityManager {
	
	private final NotificationManager notif;
	private final DatabaseManager db;
	private boolean enabled = false;
	
	public SecurityManager(NotificationManager notifications, DatabaseManager db) {
		this.notif = notifications;
		this.db = db;
		
		this.db.rawLog("Initialized system correctly.\n" + this.notif.toString());
		this.notif.trigger("Hello world");
	}
	
}
