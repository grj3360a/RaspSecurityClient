package me.security.hardware;

import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.SoftTone;

import me.security.simulation.SimulatedMode;
import me.security.simulation.Sound;

/**
 * This class use SoftTone.so lib and can't be used in a Simulated environment
 * 
 * @author Geraldes Jocelyn
 * @since 26/12/2019
 */
public class Buzzer {

	private int pin;
	private Thread multiple;
	private Thread playing;

	/**
	 * @param pin The buzzer pin
	 * @throws IllegalStateException    SoftTone manager can reject the pin
	 *                                  attribution if pin is not a PWM pin
	 * @throws IllegalArgumentException Pin must not be null
	 */
	public Buzzer(Pin pin) throws IllegalArgumentException, IllegalStateException {
		if (pin == null) throw new IllegalArgumentException("Pin can't be null");
		this.pin = pin.getAddress();
		if (SimulatedMode.IS_SIMULATED) return;
		if (SoftTone.softToneCreate(this.pin) != 0)
			throw new IllegalStateException("Unable to create softtone manager...");
	}

	/**
	 * Produce a short sound for .2 seconds
	 */
	public void shortNote() {
		this.makeSound(1000, 200);
	}

	/**
	 * Produce a high pitched sound for .4 seconds
	 */
	public void buzzHighNote() {
		this.makeSound(600, 400);
	}

	/**
	 * Produce a low pitched sound for .4 seconds
	 */
	public void buzzLowNote() {
		this.makeSound(300, 400);
	}

	/**
	 * Produce multiple low pitched sound
	 * 
	 * @param howManyTimes How many times we produce this sound
	 */
	public void multipleLow(int howManyTimes) {
		this.runInParallel(() -> {
			for (int i = 0; i < howManyTimes; i++) {
				this.buzzLowNote();
				try {
					Thread.sleep(600L);
				} catch (InterruptedException ex) {}
			}
		});
	}

	/**
	 * Produce multiple high pitched sound
	 * 
	 * @param howManyTimes How many times we produce this sound
	 */
	public void multipleHigh(int howManyTimes) {
		this.runInParallel(() -> {
			for (int i = 0; i < howManyTimes; i++) {
				this.buzzHighNote();
				try {
					Thread.sleep(600L);
				} catch (InterruptedException ex) {}
			}
		});
	}

	/**
	 * Produce disabling sound
	 */
	public void closing() {
		this.runInParallel(() -> {
			for (int i = 1000; i >= 100; i -= 300) {
				this.makeSound(i, 300);
				try {
					Thread.sleep(600L);
				} catch (InterruptedException ex) {}
			}
		});
	}

	/**
	 * Produce success sound
	 */
	public void success() {
		this.runInParallel(() -> {
			for (int i = 100; i <= 1000; i += 300) {
				this.makeSound(i, 300);
				try {
					Thread.sleep(500L);
				} catch (InterruptedException ex) {}
			}
		});
	}

	/**
	 * Produce a sound of a certain frequency for a certain duration <br>
	 * Note: We can't play multiple note at the same time<br>
	 * Calling this method will not block this thread for the duration as it uses a
	 * separated thread.
	 * 
	 * @param frequency Play sound at this frequency
	 * @param duration  Make a sound for this duration
	 */
	public void makeSound(int frequency, int duration) {
		this.stopPlaying();
		this.playing = new Thread(() -> {
			if (SimulatedMode.IS_SIMULATED) {
				Sound.tone(frequency, duration);
				return;
			}

			SoftTone.softToneWrite(this.pin, frequency);
			try {
				Thread.sleep(duration);
			} catch (InterruptedException ex) {}
			SoftTone.softToneWrite(this.pin, 0);
		});
		this.playing.start();
	}

	/**
	 * Launch a Runnable in parallel
	 * 
	 * @see multipleHigh();
	 * @param r
	 */
	private void runInParallel(Runnable r) {
		this.stopPlaying();
		this.multiple = new Thread(r);
		this.multiple.start();
	}

	/**
	 * Stop anything playing at this time
	 */
	@SuppressWarnings("deprecation")
	private void stopPlaying() {
		if (this.playing == null) return;
		if (!this.playing.isAlive()) return;
		this.playing.stop();
		
		if (SimulatedMode.IS_SIMULATED)
			return;
		
		SoftTone.softToneWrite(pin, 0);
	}

}
