package utils.dummy;

import java.lang.reflect.Field;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;

public class JUnitGPIO {
	
	/**
	 */
	public static void unprovisionAllPinsOf(Object o) {
		if(o == null) return;
		
		for(Field f : o.getClass().getDeclaredFields()) {
			if(!f.getName().equals("pin")) continue;
			if(GpioPin.class.isAssignableFrom(f.getType())) {
				f.setAccessible(true);
				
				try {
					GpioFactory.getInstance().unprovisionPin((GpioPin) f.get(o));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
}
