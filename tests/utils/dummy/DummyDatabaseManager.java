package utils.dummy;

import java.util.Arrays;
import java.util.List;

import me.security.managers.DatabaseManager;

/**
 * This class exist to protect database content from tests. It simplifies
 * testing by not making a connection for each test
 */
public class DummyDatabaseManager extends DatabaseManager {

	public DummyDatabaseManager() {
		super("notnull", "notnull", "notnull", "notnull");
	}

	@Override
	protected void initializeConnection(String domain, String db, String user, String password) {
	}

	@Override
	protected void rawLog(boolean relatedToSensor, String info) throws IllegalArgumentException {
	}

	@Override
	public List<Log> getLast10Logs() {
		return Arrays.asList(new Log(0, null, false, ""));
	}

}
