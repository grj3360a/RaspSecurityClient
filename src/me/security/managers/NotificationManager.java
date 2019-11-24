package me.security.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.security.notification.NotificationFreeAPI;
import me.security.notification.NotificationIFTTT;
import me.security.notification.NotificationSender;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 * 
 * Note: Ignoring all trigger error because they are not critical part for the moment
 */
public class NotificationManager {
	
	private final List<NotificationSender> senders;
	
	public NotificationManager() {
		senders = new ArrayList<NotificationSender>();
	}

	public void add(NotificationSender ns) {
		if(ns == null) throw new IllegalArgumentException();
		this.senders.add(ns);
	}
	
	/*
	 * Triggers
	 */
	
	public void triggerAll(String value) {
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
	
	public void triggerAll(List<String> values) {
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
	
	public void triggerFree(String message) {
		triggerSpecific(NotificationFreeAPI.class, message);
	}
	
	public void triggerIFTTT(String... values) {
		if(values.length > 3 || values.length == 0) throw new IllegalArgumentException();
		triggerSpecific(NotificationIFTTT.class, Arrays.asList(values));
	}
	
	private void triggerSpecific(Class<? extends NotificationSender> clazz, String message) {
		for(NotificationSender ns : senders) {
			if(ns.getClass().isAssignableFrom(clazz)) {
				try {
					ns.trigger(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void triggerSpecific(Class<? extends NotificationSender> clazz, List<String> values) {
		for(NotificationSender ns : senders) {
			if(ns.getClass().isAssignableFrom(clazz)) {
				try {
					ns.trigger(values);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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
