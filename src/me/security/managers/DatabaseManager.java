package me.security.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class DatabaseManager implements AutoCloseable {

	public static DatabaseManager generateFromFile() throws IOException, SQLException {
		File dbPassword = new File("database.password");

		if(!dbPassword.exists() || !dbPassword.canRead()) {
			System.out.println("Database password file doesn't exist, impossible to launch.");
			System.exit(-1);
		}
		
		if(Files.readAllLines(dbPassword.toPath()).size() != 4) {
			System.out.println("Database password file doesn't respect defined format, impossible to launch.");
			System.exit(-1);
		}
		
		List<String> dbInfo = Files.readAllLines(dbPassword.toPath());
		return new DatabaseManager(dbInfo.get(0), dbInfo.get(1), dbInfo.get(2), dbInfo.get(3));
	}
	
	//
	
	private Connection connection;
	
	public DatabaseManager(String domain, String db, String user, String password) throws SQLException, IllegalArgumentException {
		initializeConnection(domain, db, user, password);
	}
	
	protected void initializeConnection(String domain, String db, String user, String password) throws SQLException {
		DriverManager.setLoginTimeout(1);
		this.connection = DriverManager.getConnection("jdbc:mysql://" + domain + ":3306/" + db, user, password);
	}

	public void log(String info) throws IllegalArgumentException {
		this.rawLog(false, info);
	}

	public void alert(String sensorName, String alertMessage) {
		this.rawLog(true, "Detection " + sensorName + " (" + alertMessage + ")");
	}
	
	protected void rawLog(boolean relatedToSensor, String info) throws IllegalArgumentException {
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
			if(connection != null && !connection.isClosed()) connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Only for serialized reading by RestAPIManager
	 */
	public static class Log{
		@Expose public final int id;
		@Expose public final long time;
		@Expose public final boolean relatedToSensor;
		@Expose public final String info;
		
		public Log(int id, Timestamp time, boolean relatedToSensor, String info) {
			this.id = id;
			this.time = time.getTime();
			this.relatedToSensor = relatedToSensor;
			this.info = info;
		}
	}
	
}
