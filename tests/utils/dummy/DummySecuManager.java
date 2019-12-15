package utils.dummy;

import java.sql.SQLException;

import me.security.managers.DatabaseManager;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;

public class DummySecuManager extends SecuManager {

    public boolean alarmTriggered = false;
    public boolean alarmToggled = false;

	public DummySecuManager() throws UnsatisfiedLinkError, SQLException {
		super(new NotificationManager(), new DummyDatabaseManager());
	}

	public DummySecuManager(NotificationManager notif, DatabaseManager db) {
		super(notif, db);
	}
	
	@Override
	public void initializeHardware() {}//Remove hardware initialization
	
	@Override
	public void triggerAlarm(String sensorName, String alertMessage) {
		alarmTriggered = true;
	}

	@Override
	public void toggleAlarm(String code) {
		alarmToggled = !alarmToggled;
		alarmTriggered = false;
	}
	
	
}
