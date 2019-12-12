package me.security.notification;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public class NotificationFreeAPI extends NotificationSender {
	
	private final int user;
	private final String password;
	
	public NotificationFreeAPI(int user, String password) throws IllegalArgumentException {
		if(user >= 100000000 || user <= 00100000) throw new IllegalArgumentException("User id not valid size"); // Magic values to mask FreeAPI user
		if(password.length() != 14) throw new IllegalArgumentException("Password not valid size");
		this.user = user;
		this.password = password;
	}
	
	public void trigger(String message) throws ClientProtocolException, IOException, IllegalArgumentException {
		if(message.contains("&=")) throw new IllegalArgumentException("Argument contain possible exploit");
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://smsapi.free-mobile.fr/sendmsg?user=" + user + "&pass=" + password + "&msg=" + URLEncoder.encode(message, "UTF-8"));
		httpClient.execute(request);
	}

	@Override
	public void trigger(List<String> values) throws Exception {
		trigger(values.stream().collect(Collectors.joining(" ")));
	}
	
}
