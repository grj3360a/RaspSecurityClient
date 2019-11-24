package me.security.notification;

import java.util.List;

/**
 * @author Geraldes Jocelyn
 * @since 24/11/2019
 */
public abstract class NotificationSender {
	
	public abstract void trigger(String value) throws Exception;
	public abstract void trigger(List<String> values) throws Exception;
	
}
