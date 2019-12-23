package me.security.windows;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;
import com.pi4j.io.gpio.event.PinEvent;
import com.pi4j.io.gpio.event.PinListener;

import me.security.AppClient;

public class SimulatedMode {
	
	public static void setup() {
		AppClient.SIMULATED_MODE = true;
        GpioProvider provider = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(provider);
        System.out.println("Enabled system in simulated environment.");
        
        //Activating simulation.
        new Thread(() -> {	
			try {
				Thread.sleep(3000L);//Just Waiting for system to be setup.
				
				pressDigicode(provider, RaspiPin.GPIO_04, RaspiPin.GPIO_14);
				Thread.sleep(570L);
				pressDigicode(provider, RaspiPin.GPIO_03, RaspiPin.GPIO_10);
				Thread.sleep(300L);
				pressDigicode(provider, RaspiPin.GPIO_04, RaspiPin.GPIO_06);
				Thread.sleep(300L);
				pressDigicode(provider, RaspiPin.GPIO_04, RaspiPin.GPIO_10);
				Thread.sleep(300L);
				pressDigicode(provider, RaspiPin.GPIO_02, RaspiPin.GPIO_05);
			} catch (Exception e) {//We don't even care
				e.printStackTrace();
			}
		}).start();
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
	}

	
}
