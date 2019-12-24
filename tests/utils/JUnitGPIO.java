package utils;


import java.lang.reflect.Field;
import java.util.ArrayList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.SimulatedGpioProvider;
import com.pi4j.io.gpio.event.PinEvent;
import com.pi4j.io.gpio.event.PinListener;

/**
 * Used in JUnit test to define gpio state to simulate
 * @author Geraldes Jocelyn
 * @since 24/12/2019
 */
public class JUnitGPIO {
	
	/**
	 * Clean the entire GPIO controller and provider
	 * @param p The Provider to shutdown
	 * @throws IllegalStateException If the DefaultProvider is not a simulated one
	 * @throws IllegalArgumentException If the given provider is not a simulated one
	 */
	public static void cleanOut(GpioProvider p) throws IllegalStateException, IllegalArgumentException {
		if(!(GpioFactory.getDefaultProvider() instanceof SimulatedGpioProvider))
			throw new IllegalStateException("The default provider must be simulated.");
		if(!(p instanceof SimulatedGpioProvider))
			throw new IllegalArgumentException("The default provider must be simulated.");
		
		if(p != null) {
			p.removeAllListeners();
			p.shutdown();
		}
		
		GpioController gpio = GpioFactory.getInstance();
		
    	for(GpioPin pin : new ArrayList<GpioPin>(gpio.getProvisionedPins())) {
    		gpio.unprovisionPin(pin);
    	}
    	
    	gpio.removeAllListeners();
    	gpio.removeAllTriggers();
    	
    	//This is necessary as we also change the GpioProvider,
    	//so GpioController will not be affected except if we rebuild it from scratch.
		try {
			Field controller = GpioFactory.class.getDeclaredField("controller");
	    	controller.setAccessible(true);
	    	controller.set(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Simulate a button press on a digicode<br>
	 * This is needed as pressing a button on the digicode will reverse power from columns to lines
	 * @param provider The provider needed to handle pin activation
	 * @param c The column pin of this digicode
	 * @param l The line pin of this digicode
	 */
	public static void pressDigicode(GpioProvider provider, Pin c, Pin l) {
		//Create a listener that will activate once when the column is proceeded
		provider.addListener(l, new PinListener() {
			boolean onlyOnce = false;
			@Override
			public void handlePinEvent(PinEvent event) {
				if(provider.getState(l).isLow() && !onlyOnce) {
					provider.setState(l, PinState.HIGH);
					onlyOnce = true;
				}
			}
		});
		//Activate the column
		provider.setState(c, PinState.HIGH);
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {}
	}
	
}
