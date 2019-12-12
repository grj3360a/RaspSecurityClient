package me.security.hardware.sensors;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 * <b>HAVE TO BE THE SAME AS DATABASE ENUM</b>
 */
public enum SensorType {
	
	OPEN(0, "Intrusion par une fenêtre détecté !"),
	MOTION(1000L * 60 * 4, "Mouvement dans la maison détecté !"),
	HEAT(1000L * 60 * 4, "Chaleur anormale détecté !"),
	GAS(1000L * 60 * 4, "Présence anormale de gaz détecté !"),
	DIGICODE(0, "Tentative de désactivation de l'alarme hasardeuse !");
	
	private final long timeBetweenActivation;
	private final String alert;
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
