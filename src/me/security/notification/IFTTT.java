package me.security.notification;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 
 * BASED ON https://github.com/lodenrogue/JMaker
 */
public class IFTTT extends NotificationSender {
	private String eventName;
	private String key;

	public IFTTT(String eventName, String key) {
		this.eventName = eventName;
		this.key = key;
	}

	@Override
	public void trigger(String value) throws Exception {
		trigger(Arrays.asList(value));
	}

	public void trigger(List<String> values) throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("https://maker.ifttt.com/trigger/" + eventName + "/with/key/" + key);
		StringEntity params = new StringEntity(buildJson(values));
		request.addHeader("content-type", "application/json");
		request.setEntity(params);
		httpClient.execute(request);
	}

	private String buildJson(List<String> values) {
		String json = "{";
		for (int i = 0; i < 3; i++) {
			if (values.size() > i) {
				json += "\"value" + (i + 1) + "\":\"" + values.get(i) + "\"";
				if (i != 2) {
					json += ",";
				}
			}
			else {
				break;
			}
		}
		json += "}";
		return json;
	}

}