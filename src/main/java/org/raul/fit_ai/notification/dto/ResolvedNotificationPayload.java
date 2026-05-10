package org.raul.fit_ai.notification.dto;

import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import java.util.UUID;

public record ResolvedNotificationPayload(
		UUID userId,
		NotificationChannel channel,
		NotificationType type,
		String recipient,
		String subject,
		String title,
		String body
) {
}
