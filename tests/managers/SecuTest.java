package managers;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import utils.dummy.DummyDatabaseManager;

public class SecuTest {
	
	private SecuManager secu;
	
	@Before
	public void setUp() throws Exception {
		this.secu = new SecuManager(new NotificationManager(), new DummyDatabaseManager());
	}

	@After
	public void tearDown() throws Exception {
		this.secu = null;
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
