package me.security;

import me.security.managers.NotificationManager;
import me.security.managers.SecurityManager;

public class AppClient {

	public static void main(String[] args) {
		NotificationManager notif = new NotificationManager();
		SecurityManager security = new SecurityManager(notif);
		
	}

}
