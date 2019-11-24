package me.security.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager implements AutoCloseable {
	
	private final Connection connection;
	
	public DatabaseManager(String domain, String db, String user, String password) throws SQLException, ClassNotFoundException {
		connection = DriverManager.getConnection("jdbc:mysql://" + domain + ":3306/" + db, user, password);
	}
	
	public void rawLog(String info) throws IllegalArgumentException {
		if(info == null) throw new IllegalArgumentException();
		System.out.println(info);
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO `raw_log`(`log_info`) VALUES (?)");
			stmt.setString(1, info);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			//Ignore the error as error on logging will not be damageable for our code
		}
	}

	@Override
	public void close() {
		try {
			System.out.println("Closing connection...");
			if(!connection.isClosed()) connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
