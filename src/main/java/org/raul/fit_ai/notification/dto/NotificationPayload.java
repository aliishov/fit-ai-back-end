package org.raul.fit_ai.notification.dto;

import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import java.util.Map;
import java.util.UUID;

public record NotificationPayload(
		UUID userId,
		NotificationChannel channel,
		NotificationType type,
		String recipient,
		String subject,
		String title,
		Map<String, String> variables
) {
	public static NotificationPayload email(UUID userId, NotificationType type,
	                                        String recipient,  Map<String, String> variables) {
		return new NotificationPayload(userId, NotificationChannel.EMAIL,
				type, recipient, null, null, variables);
	}

	public static NotificationPayload sms(UUID userId, NotificationType type,
	                                      String recipient, Map<String, String> variables) {
		return new NotificationPayload(userId, NotificationChannel.SMS,
				type, recipient, null, null, variables);
	}

	public static NotificationPayload push(UUID userId, NotificationType type,
	                                       String deviceToken, String title, Map<String, String> variables) {
		return new NotificationPayload(userId, NotificationChannel.PUSH,
				type, deviceToken, null, title, variables);
	}
}
