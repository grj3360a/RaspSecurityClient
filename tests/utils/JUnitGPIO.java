package utils;


import java.lang.reflect.Field;
import java.util.ArrayList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.PinEvent;
import com.pi4j.io.gpio.event.PinListener;

public class JUnitGPIO {
	
	public static void cleanOut(GpioProvider p) {
		p.removeAllListeners();
		p.shutdown();
		
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
	
	public static void pressDigicode(GpioProvider provider, Pin c, Pin l) {
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
		provider.setState(c, PinState.HIGH);
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {}
	}
	
}
