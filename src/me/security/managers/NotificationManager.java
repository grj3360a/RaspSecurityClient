package me.security.managers;

import java.util.ArrayList;
import java.util.List;

import me.security.notification.NotificationSender;

public class NotificationManager {
	
	private final List<NotificationSender> senders;
	
	public NotificationManager() {
		senders = new ArrayList<NotificationSender>();
	}
	
	public void trigger(String value) {
		for(NotificationSender sender : senders) {
			try {
				sender.trigger(value);
			} catch(UnsupportedOperationException ex) {
			} catch(Exception ex) {
				ex.printStackTrace();
				//TODO Remove from senders if error occuring ?
			}
		}
	}
	
	public void trigger(List<String> values) {
		for(NotificationSender sender : senders) {
			try {
				sender.trigger(values);
			} catch(UnsupportedOperationException ex) {
			} catch(Exception ex) {
				ex.printStackTrace();
				//TODO Remove from senders if error occuring ?
			}
		}
	}

	public void add(NotificationSender ns) {
		this.senders.add(ns);
	}
	
	@Override
	public String toString() {
		String s = "Currently active notifications system:\n";
		for(NotificationSender ns : senders) {
			s += " - " + ns.getClass().getName() + "\n";
		}
		return s;
	}
	
}
