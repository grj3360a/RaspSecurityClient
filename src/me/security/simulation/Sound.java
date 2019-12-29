package me.security.simulation;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * @see https://stackoverflow.com/questions/34611134/java-beep-sound-produce-sound-of-some-specific-frequencies/34614140
 */
public class Sound {
	public static float SAMPLE_RATE = 8000f;

	public static void tone(int hz, int msecs) {
		tone(hz, msecs, 0.1);
	}

	public static void tone(int hz, int msecs, double vol) {
		byte[] buf = new byte[1];
		AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
		try {
			SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
			sdl.open(af);
			sdl.start();
			for (int i = 0; i < msecs * 8; i++) {
				double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
				buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
				sdl.write(buf, 0, 1);
			}
			sdl.drain();
			sdl.stop();
			sdl.close();
		} catch(LineUnavailableException e) {
			e.printStackTrace();
		}
	}

}