package gpio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.hardware.sensors.Sensor;
import me.security.hardware.sensors.SensorType;
import me.security.managers.SecuManager;
import utils.dummy.DummySecuManager;
import utils.dummy.JUnitGPIO;

/*
 * Explications sur les difficultées de cette classe :
 *  - La difficulté de créer des tests sans environnement WiringPi sous Windows oblige à utiliser SimulatedGpioProvider qui ne propose pas toutes les fonctionnalitées.
 *  - L'obligation de créer un DummySecuManager pour prendre le trigger de l'alarme après l'activation d'un capteur.
 *  - Le multi-threading du SimulatedGpioProvider qui ne peut pas être var sync et qui m'oblige à utiliser un Thread.sleep arbitraire
 * 
 */
public class SensorTest {
	
	private static SimulatedGpioProvider gpio;
    private static SecuManager secu;
    private static boolean alarmTriggered;
    
    private Sensor s1;
    private Sensor s2;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        gpio = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(gpio);
		secu = new DummySecuManager() {

			@Override
			public void triggerAlarm(String sensorName, String alertMessage) {
				alarmTriggered = true;
			}
			
		};
    }
	
	@Before
	public void setUp() throws Exception {
		this.s1 = new Sensor(secu, "S1", SensorType.MOTION, RaspiPin.GPIO_01);
		this.s2 = new Sensor(secu, "S2", SensorType.OPEN, RaspiPin.GPIO_02);
		alarmTriggered = false;
	}

	@After
	public void tearDown() throws Exception {
		JUnitGPIO.unprovisionAllPinsOf(this.s1);
		JUnitGPIO.unprovisionAllPinsOf(this.s2);
		Sensor.AUTO_INCREMENT = 0;
		this.s1 = null;
		this.s2 = null;
	}

	@Test
	public void testGetId() {
		assertEquals(1, this.s1.getId());
		assertEquals(2, this.s2.getId());
	}

	@Test
	public void testGetType() {
		assertEquals(SensorType.MOTION, this.s1.getType());
		assertEquals(SensorType.OPEN, this.s2.getType());
	}

	@Test
	public void testNotToggled() {
		assertFalse(this.s1.isEnabled());
	}

	@Test
	public void testToggle() {
		this.s1.toggle();
		assertTrue(this.s1.isEnabled());
	}

	@Test
	public void testTrigger() throws InterruptedException {
		this.s1.toggle();
		
		gpio.setState(RaspiPin.GPIO_01, PinState.HIGH);
		gpio.setState(RaspiPin.GPIO_01, PinState.LOW);
		
		Thread.sleep(100L);//We have to wait as the SimulatedRaspi is async thread that can't be var synchronized -_-
		
		assertTrue(alarmTriggered);
	}

}
