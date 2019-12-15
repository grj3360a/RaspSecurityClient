package utils.dummy;

import java.sql.SQLException;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;

public abstract class DummySecuManager extends SecuManager {

	public DummySecuManager() throws UnsatisfiedLinkError, SQLException {
		super(new NotificationManager(), new DummyDatabaseManager());
	}

	public DummySecuManager(NotificationManager notif, DatabaseManager db) {
		super(notif, db);
	}
	
	@Override
	public void initializeHardware() {}
	
	@Override
	public abstract void triggerAlarm(String sensorName, String alertMessage);
	
	
}
