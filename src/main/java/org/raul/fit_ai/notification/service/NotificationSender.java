package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;

public interface NotificationSender {
	void send(ResolvedNotificationPayload payload);
	NotificationChannel channel();
}
