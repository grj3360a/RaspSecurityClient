package me.security.hardware.sensors;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 *        <b>Changing this class will probably break Android Application
 *        behavior.</b> This will affect returned data of RestAPI
 */
public enum SensorType {

	OPEN(0, "Intrusion par une fenêtre détecté !"), 
	MOTION(1000L * 60 * 4, "Mouvement dans la maison détecté !"),
	HEAT(1000L * 60 * 4, "Chaleur anormale détecté !"), 
	GAS(1000L * 60 * 4, "Présence anormale de gaz détecté !");

	private final long timeBetweenActivation;
	private final String alert;

	/**
	 * @param timeBetweenActivation The time between each activation of the alarm if this type of sensor is triggered
	 * @param alert                 Alert message sended
	 */
	private SensorType(long timeBetweenActivation, String alert) {
		this.timeBetweenActivation = timeBetweenActivation;
		this.alert = alert;
	}

	public long getTimeBetweenActivation() {
		return this.timeBetweenActivation;
	}

	public String getAlertMessage() {
		return this.alert;
	}
}
