package gpio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.hardware.sensors.Sensor;
import me.security.hardware.sensors.SensorType;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import utils.JUnitGPIO;
import utils.dummy.DummyDatabaseManager;

/*
 * Explications sur les difficultées de cette classe :
 *  - La difficulté de créer des tests sans environnement WiringPi sous Windows oblige à utiliser SimulatedGpioProvider qui ne propose pas toutes les fonctionnalitées.
 *  - L'obligation de créer un DummySecuManager pour prendre le trigger de l'alarme après l'activation d'un capteur.
 *  - Le multi-threading du SimulatedGpioProvider qui ne peut pas être var sync et qui m'oblige à utiliser un Thread.sleep arbitraire
 * 
 */
public class SensorTest {

	private static SimulatedGpioProvider gpio;

	private SecuManager secu;
	private Sensor s1;
	private Sensor s2;

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
		Sensor.AUTO_INCREMENT = 100;
		this.s1 = new Sensor(secu, "S1", SensorType.MOTION, RaspiPin.GPIO_13);
		this.s2 = new Sensor(secu, "S2", SensorType.OPEN, RaspiPin.GPIO_14);
	}

	@After
	public void tearDown() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		Sensor.AUTO_INCREMENT = 0;
		this.s1 = null;
		this.s2 = null;
		this.secu = null;
	}

	@Test
	public void testGetId() {
		assertEquals(101, this.s1.getId());
		assertEquals(102, this.s2.getId());
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

	/**
	 * This test is very long as we have to wait Sensor.WAIT_BEFORE_TRIGGER
	 */
	@Test
	public void testTrigger() throws InterruptedException {
		this.secu.toggleAlarm("JUnit");
		this.s1.toggle();

		gpio.setState(RaspiPin.GPIO_13, PinState.HIGH);
		gpio.setState(RaspiPin.GPIO_13, PinState.LOW);

		Thread.sleep(300L);// We have to wait as the SimulatedRaspi is async thread that can't be var
							// synchronized -_-
		assertTrue(this.s1.isTriggering());

		Thread.sleep(Sensor.WAIT_BEFORE_TRIGGER);
		assertTrue(this.secu.hasAlarmTriggered());
	}

}
