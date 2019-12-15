package me.security.notification;

import java.util.List;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public abstract class NotificationSender {
	
	/**
	 * Send a notification to this particular implementation of NotificationSender
	 * @param value The message to send
	 * @throws Exception If value doesn't respect format of NotificationSender
	 */
	public abstract void trigger(String value) throws Exception;
	public abstract void trigger(List<String> values) throws Exception;
	
}
