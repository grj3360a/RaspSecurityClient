package me.security.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;

import me.security.hardware.sensors.SensorType;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class DatabaseManager implements AutoCloseable {
	
	private final Connection connection;
	
	public DatabaseManager(String domain, String db, String user, String password) throws SQLException, ClassNotFoundException {
		connection = DriverManager.getConnection("jdbc:mysql://" + domain + ":3306/" + db, user, password);
	}
	
	public void log(String info) throws IllegalArgumentException {
		this.rawLog(false, info);
	}

	public void alert(String sensorName, SensorType type) {
		this.rawLog(true, "Detection " + sensorName + " (" + type.getAlertMessage() + ")");
	}
	
	private void rawLog(boolean relatedToSensor, String info) throws IllegalArgumentException {
		if(info == null) throw new IllegalArgumentException();
		System.out.println(info);
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO `logs`(`relatedToSensor`,`log_info`) VALUES (?,?)");
			stmt.setBoolean(1, relatedToSensor);
			stmt.setString(2, info);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			//Ignore the error as error on logging will not be damageable for our code
		}
	}
	
	public List<Log> getLast10Logs(){
		try {
			List<Log> logs = new ArrayList<DatabaseManager.Log>();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `logs` ORDER BY `id_log` DESC LIMIT 10");
			
			while(rs.next()) {
				logs.add(new Log(rs.getInt(1), rs.getTimestamp(2), rs.getBoolean(3), rs.getString(4)));
			}
			
			return logs;
		} catch (SQLException e) {
			e.printStackTrace();
			return Arrays.asList(new Log(0, new Timestamp(System.currentTimeMillis()), false, "Error on DB connection, unable to retrieve logs."));
		} catch (Error e) {
			e.printStackTrace();
			return Arrays.asList(new Log(0, new Timestamp(System.currentTimeMillis()), false, "Error " + e.getMessage()));
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
	
	/**
	 * Only for serialized reading by RestAPIManager
	 */
	public static class Log{
		@Expose private final int id;
		@Expose private final long time;
		@Expose private final boolean relatedToSensor;
		@Expose private final String info;
		
		private Log(int id, Timestamp time, boolean relatedToSensor, String info) {
			this.id = id;
			this.time = time.getTime();
			this.relatedToSensor = relatedToSensor;
			this.info = info;
		}
	}
	
}
