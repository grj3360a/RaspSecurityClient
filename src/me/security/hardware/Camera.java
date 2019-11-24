package me.security.hardware;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

/**
 * @author Ekinoxx
 * @since 24/11/2019
 * 
 * As of this date, can't test this because we still don't have a camera.
 * @see https://github.com/Hopding/JRPiCam
 */
public class Camera {
	
	public Camera() throws FailedToRunRaspistillException {
		RPiCamera piCamera = new RPiCamera("/tmp");
		
		try {
			BufferedImage image = piCamera.takeBufferedStill();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
