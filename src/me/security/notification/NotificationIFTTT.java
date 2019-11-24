package me.security.notification;

import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class NotificationIFTTT extends NotificationSender {
	
	private final HttpClient httpClient;
	private String event;
	private String key;

	public NotificationIFTTT(String event, String key) {
		this.httpClient = HttpClientBuilder.create().build();
		this.event = event;
		this.key = key;
	}

	@Override
	public void trigger(String value) throws Exception {
		trigger(Arrays.asList(value));
	}

	public void trigger(List<String> values) throws Exception {
		HttpPost request = new HttpPost("https://maker.ifttt.com/trigger/" + event + "/with/key/" + key);
		request.addHeader("content-type", "application/json");
		request.setEntity(new StringEntity(buildJson(values)));
		HttpResponse hr = httpClient.execute(request);
		
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

}