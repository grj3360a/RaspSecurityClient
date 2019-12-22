package managers;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import utils.JUnitGPIO;
import utils.dummy.DummyDatabaseManager;

public class SecuTest {

	private static SimulatedGpioProvider gpio;
	
    @BeforeClass
    public static void setUpClass() throws Exception {
        gpio = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(gpio);
    }
    
    @AfterClass
    public static void tearDownClass() {
		JUnitGPIO.cleanOut(gpio);
    	gpio = null;
    }
    
    //
	
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
