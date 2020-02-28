package me.security.managers;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ServerSyncManager implements Closeable {
	
	private static final String URL = "https://ekinoxx.ovh/";
	private final SecuManager secu;
	
	private boolean failedOnLastTry;
	private final List<String> waitingLogs;
	private final List<String> waitingNotifs;
	
	public ServerSyncManager(SecuManager secu) {
		if(secu == null) throw new IllegalArgumentException();
		this.secu = secu;
		
		this.failedOnLastTry = false;
		this.waitingLogs = new ArrayList<String>();
		this.waitingNotifs = new ArrayList<String>();
	}

	private void retryAllFailed() {
		
	}

	public void log(String message) {
		this.callServer("notif", message, this.waitingLogs);
	}

	public void triggerNotif(String message) {
		this.callServer("notif", message, this.waitingNotifs);
	}
	
	private void callServer(String uri, String message, List<String> backupOnFail) {
		if(uri == null) throw new IllegalArgumentException();
		if(uri.length() == 0) throw new IllegalArgumentException();
		if(message == null) throw new IllegalArgumentException();
		if(message.length() == 0) throw new IllegalArgumentException();
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(URL + uri + "?msg=" + URLEncoder.encode(message, Charset.forName("UTF-8")));
		request.addHeader("appPassword", "eaz897hfg654kiu714sf32d1");
		
		try {
			HttpResponse hr = httpClient.execute(request);
			if(hr.getStatusLine().getStatusCode() == 200) {
				this.failedOnLastTry = false;
				this.retryAllFailed();
			}
			
		} catch (ClientProtocolException e) {
			//Malformatted Request is pretty bad.
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			this.failedOnLastTry = true;
			backupOnFail.add(message);
		}
	}

	public void close() {
		System.out.println("Closing ServerSyncManager...");
		if(this.failedOnLastTry) {
			System.out.println("Seems we are still offline. Not good!");
		}
	}
	
}
