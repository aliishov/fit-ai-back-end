package org.raul.fit_ai.notification.service.events;

import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.service.NotificationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationEventListener {

	NotificationService notificationService;

	@KafkaListener(
			topics = "${app.kafka.topics.critical.name}",
			groupId = "critical-notification-group",
			concurrency = "3"
	)
	public void handleCritical(NotificationPayload payload) {
		log.info("Consuming critical notification type=[{}] user=[{}]",
				payload.type(), payload.userId());
		try {
			notificationService.send(payload);
		} catch (Exception e) {
			log.error("Failed to process critical notification type=[{}] user=[{}]",
					payload.type(), payload.userId(), e);
			throw e;
		}
	}

	@KafkaListener(
			topics = "${app.kafka.topics.transactional.name}",
			groupId = "transactional-notification-group",
			concurrency = "3"
	)
	public void handleTransactional(NotificationPayload payload) {
		log.info("Consuming transactional notification type=[{}] user=[{}]",
				payload.type(), payload.userId());
		try {
			notificationService.send(payload);
		} catch (Exception e) {
			log.error("Failed to process transactional notification type=[{}] user=[{}]",
					payload.type(), payload.userId(), e);
			throw e;
		}
	}

	@KafkaListener(
			topics = "${app.kafka.topics.scheduled.name}",
			groupId = "scheduled-notification-group",
			concurrency = "5"
	)
	public void handleScheduled(NotificationPayload payload) {
		log.info("Consuming scheduled notification type=[{}] user=[{}]",
				payload.type(), payload.userId());
		try {
			notificationService.send(payload);
		} catch (Exception e) {
			log.warn("Failed to process scheduled notification type=[{}] user=[{}] — skipping",
					payload.type(), payload.userId(), e);
		}
	}
}
