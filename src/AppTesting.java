import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class AppTesting {

	public static final GpioController GPIO = GpioFactory.getInstance();
	
	public static void main(String[] args) {
		
		final char[][] keys = {{'1', '2', '3', 'A'},
						 	   {'4', '5', '6', 'B'},
						 	   {'7', '8', '9', 'C'},
						 	   {'*', '0', '#', 'D'}};
		
		/*GpioPinDigitalOutput blueLed = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
		GpioPinDigitalOutput yellowLed = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW);
		final GpioPinDigitalOutput greenLed = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW);*/
		final GpioPinDigitalOutput redLed = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW);
		
		//final GpioPinDigitalOutput buzzer = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_26, PinState.LOW);
		
		GpioPinDigitalInput movement = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_29);
		
		GpioPinDigitalMultipurpose padl1 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_14, "0", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padl2 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_10, "1", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padl3 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_06, "2", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padl4 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_05, "3", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padc1 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_04, "0", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padc2 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_03, "1", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padc3 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, "2", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
		GpioPinDigitalMultipurpose padc4 = GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, "3", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);

		final GpioPinDigitalMultipurpose[] pad = {padc1, padc2, padc3, padc4, padl1, padl2, padl3, padl4};
		final GpioPinDigitalMultipurpose[] padc = {padc1, padc2, padc3, padc4};
		final GpioPinDigitalMultipurpose[] padl = {padl1, padl2, padl3, padl4};
		
		for(GpioPinDigitalMultipurpose g : pad) {
			g.setDebounce(50);
		}
		
		/*yellowLed.blink(600, Long.MAX_VALUE);
		greenLed.blink(600, Long.MAX_VALUE);
		blueLed.blink(600, Long.MAX_VALUE);*/
		
		movement.addListener(new GpioPinListenerDigital() {
			
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getEdge() == PinEdge.FALLING) return;
				redLed.pulse(6000);
			}
		});
		
		for(final GpioPinDigitalOutput pc : padl) {
			pc.setState(true);
		}
		
		for(final GpioPinDigitalInput pc : padc) {
			pc.addListener(new GpioPinListenerDigital() {
				
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
					if(event.getEdge() == PinEdge.FALLING) return;
					System.out.println("Detection...");
					if(!pc.isMode(PinMode.DIGITAL_INPUT)) return;
					for(GpioPinDigitalMultipurpose po : padc) {
						po.setMode(PinMode.DIGITAL_OUTPUT);
						po.setState(true);
					}
					for(GpioPinDigitalMultipurpose po : padl) {
						po.setState(false);
						po.setMode(PinMode.DIGITAL_INPUT);
					}

					for(GpioPinDigitalMultipurpose po : padl) {
						if(po.isHigh()) {
							System.out.print(keys[Integer.parseInt(po.getName())][Integer.parseInt(pc.getName())]);
						}
					}
					for(GpioPinDigitalMultipurpose po : padl) {
						po.setMode(PinMode.DIGITAL_OUTPUT);
						po.setState(true);
					}
					for(GpioPinDigitalMultipurpose po : padc) {
						po.setState(false);
						po.setMode(PinMode.DIGITAL_INPUT);
					}
					System.out.println();
				}
			});
		}
		
		while(true) {
			try {
				Thread.sleep(50);
			}catch(Throwable e) {}
		}
	}

}
