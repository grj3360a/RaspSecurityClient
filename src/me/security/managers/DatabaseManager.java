package me.security.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
	
	private Connection connection;
	
	public DatabaseManager(String domain, String db, String user, String password) throws SQLException {
		connection = DriverManager.getConnection(domain, user, password);
	}
	
}
