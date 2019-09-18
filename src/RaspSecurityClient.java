import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class RaspSecurityClient {

	public static final GpioController GPIO = GpioFactory.getInstance();
	
	public static void main(String[] args) {
		GpioPinDigitalOutput myLed = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_25);
		
		while(true) {
			myLed.toggle();
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {}
		}
	}

}
