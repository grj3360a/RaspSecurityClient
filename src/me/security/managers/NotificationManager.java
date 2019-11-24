package me.security.managers;

import java.util.ArrayList;
import java.util.List;

import me.security.notification.NotificationSender;

public class NotificationManager {
	
	private final List<NotificationSender> senders;
	
	public NotificationManager() {
		senders = new ArrayList<NotificationSender>();
	}
	
	public void trigger(String value) throws Exception{
		for(NotificationSender sender : senders) {
			sender.trigger(value);
		}
	}
	
	public void trigger(List<String> values) throws Exception{
		for(NotificationSender sender : senders) {
			sender.trigger(values);
		}
	}
	
}
