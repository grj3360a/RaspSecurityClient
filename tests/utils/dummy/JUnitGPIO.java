package utils.dummy;

import java.lang.reflect.Field;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.PinEvent;
import com.pi4j.io.gpio.event.PinListener;

public class JUnitGPIO {
	
	/**
	 */
	public static void unprovisionAllPinsOf(Object o) {
		if(o == null) return;
		
		for(Field f : o.getClass().getDeclaredFields()) {
			if(GpioPin.class.isAssignableFrom(f.getType())) {
				f.setAccessible(true);
				
				try {
					System.out.println("unpro: " + f.getName());
					GpioFactory.getInstance().unprovisionPin((GpioPin) f.get(o));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else if(GpioPin[].class.isAssignableFrom(f.getType())){
				f.setAccessible(true);
				try {
					GpioPin[] pins = (GpioPin[]) f.get(o);
					for(GpioPin pin : pins) {
						GpioFactory.getInstance().unprovisionPin(pin);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
