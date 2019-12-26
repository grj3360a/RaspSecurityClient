package me.security.hardware;

import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.SoftTone;

import me.security.simulation.SimulatedMode;

public class Buzzer {
	
	private int pin;
	private Thread multiple;
	private Thread playing;

	public Buzzer(Pin pin) throws IllegalStateException {
		this.pin = pin.getAddress();
		if(SimulatedMode.IS_SIMULATED)
			return;
		if(SoftTone.softToneCreate(this.pin) != 0)
			throw new IllegalStateException("Unable to create softtone manager...");
	}
	
	public void shortNote() {
		this.makeSound(1000, 200L);
	}
	
	public void buzzHighNote() {
		this.makeSound(600, 400L);
	}
	
	public void buzzLowNote() {
		this.makeSound(300, 400L);
	}
	
	public void multipleLow(int howManyTimes) {
		this.runInParallel(() -> {
			for (int i = 0; i < howManyTimes; i++) {
				this.buzzLowNote();
				try {
					Thread.sleep(600L);
				} catch(InterruptedException ex) {}
			}
		});
	}
	
	public void multipleHigh(int howManyTimes) {
		this.runInParallel(() -> {
			for (int i = 0; i < howManyTimes; i++) {
				this.buzzHighNote();
				try {
					Thread.sleep(600L);
				} catch(InterruptedException ex) {}
			}
		});
	}
	
	/**
	 * @param frequency Play sound at this frequency
	 * @param duration Make a sound for this duration
	 */
	public void makeSound(int frequency, long duration) {
		this.stopPlaying();
		this.playing = new Thread(() -> {
			if(SimulatedMode.IS_SIMULATED)
				return;
			SoftTone.softToneWrite(this.pin, frequency);
			try {
				Thread.sleep(duration);
			} catch(InterruptedException ex) {}
			SoftTone.softToneWrite(this.pin, 0);
		});
		this.playing.start();
	}
	
	private void runInParallel(Runnable r) {
		this.stopPlaying();
		this.multiple = new Thread(r);
		this.multiple.start();
	}
	
	@SuppressWarnings("deprecation")
	private void stopPlaying() {
		if(this.playing == null) return;
		if(!this.playing.isAlive()) return;
		this.playing.stop();
		if(SimulatedMode.IS_SIMULATED)
			return;
		SoftTone.softToneWrite(pin, 0);
	}
	
}
