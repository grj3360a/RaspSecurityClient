package me.security.simulation;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import utils.JUnitGPIO;

/**
 * Allows to launch this program without Raspberry
 * @author Geraldes Jocelyn
 * @since 24/12/2019
 */
public class SimulatedMode {
	
	/**
	 * Setup the SimulatedMode<br><br>
	 * Define the GpioProvider to simulated<br>
	 * Then simulate pressing button and displaying led state
	 */
	public static void setup() {
        GpioProvider provider = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(provider);
        System.out.println("Enabled system in simulated environment.");
        
        //Activating simulation.
        new Thread(() -> {	
			try {
				Thread.sleep(3000L);//Just Waiting for system to be setup.
				
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_03, RaspiPin.GPIO_26);
				Thread.sleep(570L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_03, RaspiPin.GPIO_23);
				Thread.sleep(300L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_02, RaspiPin.GPIO_23);
				Thread.sleep(300L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_01, RaspiPin.GPIO_22);
				Thread.sleep(300L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_01, RaspiPin.GPIO_21);
				Thread.sleep(3000L);//Just Waiting for system to be setup.
				
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_03, RaspiPin.GPIO_26);
				Thread.sleep(570L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_03, RaspiPin.GPIO_23);
				Thread.sleep(300L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_02, RaspiPin.GPIO_23);
				Thread.sleep(300L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_01, RaspiPin.GPIO_22);
				Thread.sleep(300L);
				JUnitGPIO.pressDigicode(provider, RaspiPin.GPIO_01, RaspiPin.GPIO_21);
			} catch (Exception e) {//We don't even care
				e.printStackTrace();
			}
		}).start();
	}
	
}
