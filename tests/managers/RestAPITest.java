package managers;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.security.managers.RestAPIManager;
import utils.dummy.DummySecuManager;

public class RestAPITest {
	
	private RestAPIManager restapi;

	@Before
	public void setUp() throws Exception {
		//this.restapi = new RestAPIManager(new DummySecuManager());
	}

	@After
	public void tearDown() throws Exception {
		this.restapi = null;
	}

	@Test
	public void testRestAPIManager() {
		fail("Not yet implemented");
	}

}
