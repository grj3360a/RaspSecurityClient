package me.security.managers;

import java.io.Closeable;

public class ServerSyncManager implements Closeable {
	
	private final SecuManager secu;
	
	public ServerSyncManager(SecuManager secu) {
		this.secu = secu;
	}

	public void log(String value) {
		
	}

	public void alert(String sensorName, String alertMessage) {
		
	}

	public void triggerAll(String value) {
		
	}

	public void triggerIFTTT(String value) {
		
	}

	public void triggerFree(String value) {
		
	}

	public void close() {
		
	}
	
}
