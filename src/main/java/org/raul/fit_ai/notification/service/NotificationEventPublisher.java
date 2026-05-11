package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class NotificationEventPublisher {

	final KafkaTemplate<String, NotificationPayload> kafkaTemplate;

	@Value("${app.kafka.topics.critical.name}")
	String criticalTopic;

	@Value("${app.kafka.topics.transactional.name}")
	String transactionalTopic;

	@Value("${app.kafka.topics.scheduled.name}")
	String scheduledTopic;

	private static final Set<NotificationType> CRITICAL_TYPES = Set.of(
			NotificationType.OTP,
			NotificationType.PASSWORD_CHANGED,
			NotificationType.EMAIL_VERIFICATION,
			NotificationType.PHONE_VERIFICATION
	);

	private static final Set<NotificationType> TRANSACTIONAL_TYPES = Set.of(
			NotificationType.WELCOME,
			NotificationType.LOGIN_SUCCESS,
			NotificationType.WORKOUT_PLAN_GENERATED
	);

	public void publish(NotificationPayload payload) {
		String topic = resolveTopic(payload.type());

		kafkaTemplate.send(topic, payload.userId().toString(), payload)
				.whenComplete((result, ex) -> {
					if (ex != null) {
						log.error("Failed to publish notification type=[{}] user=[{}] topic=[{}]",
								payload.type(), payload.userId(), topic, ex);
					} else {
						log.info("Published notification type=[{}] user=[{}] partition=[{}] offset=[{}]",
								payload.type(),
								payload.userId(),
								result.getRecordMetadata().partition(),
								result.getRecordMetadata().offset());
					}
				});
	}

	public void publishCritical(NotificationPayload payload) {
		if (!CRITICAL_TYPES.contains(payload.type())) {
			throw new IllegalArgumentException(
					"publishCritical called with non-critical type: " + payload.type()
			);
		}

		try {
			SendResult<String, NotificationPayload> result = kafkaTemplate
					.send(criticalTopic, payload.userId().toString(), payload)
					.get(5, TimeUnit.SECONDS);

			log.info("Published critical notification type=[{}] user=[{}] partition=[{}] offset=[{}]",
					payload.type(),
					payload.userId(),
					result.getRecordMetadata().partition(),
					result.getRecordMetadata().offset());

		} catch (TimeoutException e) {
			log.error("Timeout publishing critical notification type=[{}] user=[{}]",
					payload.type(), payload.userId(), e);
			throw new NotificationException("Notification service timeout — please try again");

		} catch (ExecutionException e) {
			log.error("Failed to publish critical notification type=[{}] user=[{}]",
					payload.type(), payload.userId(), e);
			throw new NotificationException("Failed to queue notification");

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NotificationException("Interrupted while sending notification");
		}
	}

	private String resolveTopic(NotificationType type) {
		if (CRITICAL_TYPES.contains(type)) {
			return criticalTopic;
		}

		if (TRANSACTIONAL_TYPES.contains(type)) {
			return transactionalTopic;
		}

		return scheduledTopic;
	}
}
