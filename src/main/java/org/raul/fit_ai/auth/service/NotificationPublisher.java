package org.raul.fit_ai.auth.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.raul.fit_ai.common.service.NotificationEventPublisher;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationPublisher {

	NotificationEventPublisher notificationEventPublisher;

	public void publish(NotificationPayload payload) {
		notificationEventPublisher.publish(payload);
	}

	public void publishCritical(NotificationPayload payload) {
		notificationEventPublisher.publishCritical(payload);
	}
}
