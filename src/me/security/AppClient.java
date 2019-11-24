package me.security;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.SecurityManager;

public class AppClient {

	public static void main(String[] args) {
		DatabaseManager db = new DatabaseManager();
		NotificationManager notif = new NotificationManager();
		SecurityManager security = new SecurityManager(notif, db);
		
	}

}
