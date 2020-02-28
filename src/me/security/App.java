package me.security;

import java.io.File;
import java.io.IOException;

import me.security.managers.SecuManager;
import me.security.simulation.SimulatedMode;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class App {

	/**
	 * The main entry point of RaspSecurity. Utilization of the argument --simulated
	 * is needed if running this on Windows
	 */
	public static void main(String[] args) throws UnsatisfiedLinkError, IOException {
		System.out.println("Launching RaspSecurityClient... (" + new File(".").getAbsolutePath() + ")");
		for (String s : args) {
			if (s.toLowerCase().equals("--simulated")) {
				SimulatedMode.setup();
			}
		}

		/*
		 * Security handler
		 */
		SecuManager secu = new SecuManager();

		if (SimulatedMode.IS_SIMULATED)
			SimulatedMode.launchSimulatedWindow(secu);

		// Adding closing mechanism to shutdown DB connection
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Closing RaspSecurityClient...");
			secu.close();
		}));

		System.out.println("Started successfuly.");
	}
}
