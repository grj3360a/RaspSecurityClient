package gpio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.hardware.Digicode;
import me.security.managers.SecuManager;
import utils.dummy.DummySecuManager;
import utils.dummy.JUnitGPIO;

public class DigicodeTest {
	
	private static SimulatedGpioProvider gpio;
    private static SecuManager secu;
    private static boolean alarmToggled;
    
    private Digicode digi;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        gpio = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(gpio);
		secu = new DummySecuManager() {

			@Override
			public void triggerAlarm(String sensorName, String alertMessage) {}

			@Override
			public void toggleAlarm(String code) {
				alarmToggled = !alarmToggled;
			}
			
		};
    }
	
	@Before
	public void setUp() throws Exception {
		this.digi = new Digicode(
				secu, 
				"1574", 
				new Pin[]{RaspiPin.GPIO_14, RaspiPin.GPIO_10, RaspiPin.GPIO_06, RaspiPin.GPIO_05}, 
				new Pin[]{RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_02, RaspiPin.GPIO_00}
				);
		alarmToggled = false;
	}

	@After
	public void tearDown() throws Exception {
		JUnitGPIO.unprovisionAllPinsOf(this.digi);
		this.digi = null;
	}

	@Test
	public void testToggleOnAlarm() {
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertTrue(alarmToggled);
	}

	@Test
	public void testToggleOnWithErrorAlarm() {
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_10);//6

		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_05);//*

		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertTrue(alarmToggled);
	}

	@Test
	public void testAddPasscodeThenToggleOn() {
		this.digi.addPasscode("4751");
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertTrue(alarmToggled);
	}

	@Test
	public void testToggleOnThenOffAlarm() {
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertFalse(alarmToggled);
	}

}
