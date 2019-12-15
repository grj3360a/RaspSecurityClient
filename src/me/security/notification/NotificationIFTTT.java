package me.security.notification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Notification implementation of the IFFT api
 * @see https://ifttt.com/applets/106799825d
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class NotificationIFTTT extends NotificationSender {
	
	public static NotificationIFTTT generateFromFile() {
		File iftttPwd = new File("./ifttt.password");

		if(!iftttPwd.exists() || !iftttPwd.canRead()) {
			System.out.println("IFTTT password file doesn't exist");
			return null;
		}
		
		List<String> iftttInfo;
		try {
			iftttInfo = Files.readAllLines(iftttPwd.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
			
		if(iftttInfo.size() != 2) {
			System.out.println("IFTTT password file doesn't respect defined format");
			return null;
		}
		
		return new NotificationIFTTT(iftttInfo.get(0), iftttInfo.get(1));
	}
	
	//
	
	private final HttpClient httpClient;
	private String event;
	private String key;

	public NotificationIFTTT(String event, String key) {
		if(event == null) throw new IllegalArgumentException("Event must not be null");
		if(event.length() == 0) throw new IllegalArgumentException("Event must not be empty");
		if(key == null) throw new IllegalArgumentException("Key must not be null");
		if(key.length() != 22) throw new IllegalArgumentException("Key must be of size 22 characters");
		
		this.httpClient = HttpClientBuilder.create().build();
		this.event = event;
		this.key = key;
	}

	@Override
	public void trigger(String message) throws Exception {
		this.trigger(Arrays.asList(message));
	}

	@Override
	public void trigger(List<String> values) throws Exception {
		if(values == null) throw new IllegalArgumentException("values is null");
		if(values.isEmpty()) throw new IllegalArgumentException("values list is empty");
		for(String v : values)
			if(v == null || v.length() == 0) throw new IllegalArgumentException("values contains null or empty value");
		
		HttpPost request = new HttpPost("https://maker.ifttt.com/trigger/" + this.event + "/with/key/" + this.key);
		request.addHeader("content-type", "application/json");
		request.setEntity(new StringEntity(buildJson(values)));
		HttpResponse hr = this.httpClient.execute(request);
		
		if(hr.getStatusLine().getStatusCode() != 200) throw new Exception("Response from IFTTT doesn't validate!");
	}

	private String buildJson(List<String> values) {
		if(values.size() > 3) throw new IllegalArgumentException();
		
		String json = "";
		for (int i = 0; i < values.size(); i++) {
			json += String.format("\"value%d\":\"%s\"", i+1, values.get(i));
			if (i < values.size() - 1) json += ",";
		}
		
		return "{" + json + "}";
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof NotificationIFTTT)) return false;
		NotificationIFTTT notif = (NotificationIFTTT) o;
		return notif.event.equals(this.event) && notif.key.equals(this.key);
	}

}