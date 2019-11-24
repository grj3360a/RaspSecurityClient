package me.security.managers;

public class SecurityManager {
	
	private final NotificationManager notif;
	private boolean enabled = true;
	
	public SecurityManager(NotificationManager notifications) {
		this.notif = notifications;
	}
	
}
