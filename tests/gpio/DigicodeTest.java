package gpio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.hardware.Digicode;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import utils.JUnitGPIO;
import utils.dummy.DummyDatabaseManager;

public class DigicodeTest {
	
	private static SimulatedGpioProvider gpio;
	
    private SecuManager secu;
    private Digicode digi;
    
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
	
	@Before
	public void setUp() throws Exception {
		this.secu = new SecuManager(new NotificationManager(), new DummyDatabaseManager());
		this.digi = this.secu.getDigicode();
	}

	@After
	public void tearDown() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		this.digi = null;
		this.secu = null;
	}
	
	@Test
	public void testToggleOnAlarm() {
		JUnitGPIO.pressDigicode('1');
		JUnitGPIO.pressDigicode('5');
		JUnitGPIO.pressDigicode('7');
		JUnitGPIO.pressDigicode('4');
		
		JUnitGPIO.pressDigicode('#');
		
		assertTrue(secu.getDigicode().isActivating());
	}

	@Test
	public void testToggleOnWithErrorAlarm() {
		JUnitGPIO.pressDigicode('1');
		JUnitGPIO.pressDigicode('5');
		JUnitGPIO.pressDigicode('6');
		

		JUnitGPIO.pressDigicode('*');

		JUnitGPIO.pressDigicode('1');
		JUnitGPIO.pressDigicode('5');
		JUnitGPIO.pressDigicode('7');
		JUnitGPIO.pressDigicode('4');
		
		JUnitGPIO.pressDigicode('#');

		assertTrue(secu.getDigicode().isActivating());
	}

	@Test
	public void testAddPasscodeThenToggleOn() {
		this.digi.addPasscode("1234");
		JUnitGPIO.pressDigicode('1');
		JUnitGPIO.pressDigicode('2');
		JUnitGPIO.pressDigicode('3');
		JUnitGPIO.pressDigicode('4');
		
		JUnitGPIO.pressDigicode('#');

		assertTrue(secu.getDigicode().isActivating());
	}

	@Test
	public void testToggleOnThenOffAlarm() {
		JUnitGPIO.pressDigicode('1');
		JUnitGPIO.pressDigicode('5');
		JUnitGPIO.pressDigicode('7');
		JUnitGPIO.pressDigicode('4');
		
		JUnitGPIO.pressDigicode('#');

		JUnitGPIO.pressDigicode('1');
		JUnitGPIO.pressDigicode('5');
		JUnitGPIO.pressDigicode('7');
		JUnitGPIO.pressDigicode('4');
		
		JUnitGPIO.pressDigicode('#');
		
		assertFalse(secu.isEnabled());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullSecuManager() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(null, "1234", 
				new Pin[] {RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[] {RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullCode() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(new SecuManager(new NotificationManager(), new DummyDatabaseManager()), null, 
				new Pin[] {RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[] {RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCodeInvalid() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(new SecuManager(new NotificationManager(), new DummyDatabaseManager()), "TAILLE_INVALIDE", 
				new Pin[] {RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[] {RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLinesNull() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(new SecuManager(new NotificationManager(), new DummyDatabaseManager()), "1234", 
				null, 
				new Pin[] {RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLinesInvalid() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(new SecuManager(new NotificationManager(), new DummyDatabaseManager()), "1234", 
				new Pin[] {RaspiPin.GPIO_14}, 
				new Pin[] {RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testColumnsNull() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(new SecuManager(new NotificationManager(), new DummyDatabaseManager()), "1234", 
				new Pin[] {RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testColumnsInvalid() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		new Digicode(new SecuManager(new NotificationManager(), new DummyDatabaseManager()), "1234", 
				new Pin[] {RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[] {RaspiPin.GPIO_02,RaspiPin.GPIO_00});
	}
	

}
