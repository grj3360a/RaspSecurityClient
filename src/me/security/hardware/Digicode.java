package me.security.hardware;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class Digicode extends HardwareElement {
	
	private static final char[][] keys = {	{'1', '2', '3', 'A'},
					 	   					{'4', '5', '6', 'B'},
					 	   					{'7', '8', '9', 'C'},
					 	   					{'*', '0', '#', 'D'}};
	
	private char[] code;
	
	public Digicode() {
		
	}
	
}
