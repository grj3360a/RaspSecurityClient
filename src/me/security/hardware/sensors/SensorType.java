package me.security.hardware.sensors;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 * <b>HAVE TO BE THE SAME AS DATABASE ENUM</b>
 */
public enum SensorType {
	
	OPEN("Intrusion par une fenêtre détecté !"),
	MOTION("Mouvement dans la maison détecté !"),
	HEAT("Chaleur anormale détecté !"),
	GAS("Présence anormale de gaz détecté !");
	
	private final String alert;
	private SensorType(String alert) {
		this.alert = alert;
	}
	
	public String getAlertMessage() {
		return this.alert;
	}
}
