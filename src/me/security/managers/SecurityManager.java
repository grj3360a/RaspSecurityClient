package me.security.managers;

public class SecurityManager {
	
	private final NotificationManager notif;
	private final DatabaseManager db;
	private boolean enabled = true;
	
	public SecurityManager(NotificationManager notifications, DatabaseManager db) {
		this.notif = notifications;
		this.db = db;
	}
	
}
