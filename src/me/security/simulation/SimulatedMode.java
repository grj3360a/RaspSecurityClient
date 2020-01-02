package me.security.simulation;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.managers.SecuManager;

/**
 * Allows to launch this program without Raspberry
 * @author Geraldes Jocelyn
 * @since 24/12/2019
 */
public class SimulatedMode {
	
	public static boolean IS_SIMULATED = false;
	
	/**
	 * Setup the SimulatedMode<br><br>
	 * Define the GpioProvider to simulated<br>
	 * Then simulate pressing button and displaying led state
	 */
	public static void setup() {
		IS_SIMULATED = true;
        GpioProvider provider = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(provider);
        System.out.println("Enabled system in simulated environment.");
	}

	public static void launchSimulatedWindow(SecuManager secu) {
		if(!IS_SIMULATED) return;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new WindowedSimulator(secu).setVisible(true);
			}
		});
	}
	
}
