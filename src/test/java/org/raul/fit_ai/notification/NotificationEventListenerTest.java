package org.raul.fit_ai.notification;

import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.raul.fit_ai.notification.service.NotificationService;
import org.raul.fit_ai.notification.service.events.NotificationEventListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("NotificationEventListener Integration Tests")
class NotificationEventListenerTest {

	private NotificationEventListener notificationEventListener;

	@Mock
	private NotificationService notificationService;

	private UUID userId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		notificationEventListener = new NotificationEventListener(notificationService);
		userId = UUID.randomUUID();
	}

	@Test
	@DisplayName("Should handle critical notification event")
	void handleCriticalNotificationEvent() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of("name", "John")
		);

		notificationEventListener.handleCritical(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should handle transactional notification event")
	void handleTransactionalNotificationEvent() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.REMINDER,
				"test@example.com",
				Map.of("orderId", "12345")
		);

		notificationEventListener.handleTransactional(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should handle scheduled notification event")
	void handleScheduledNotificationEvent() {
		NotificationPayload payload = NotificationPayload.push(
				userId,
				NotificationType.REMINDER,
				"Workout Time",
				Map.of("activity", "Running")
		);

		notificationEventListener.handleScheduled(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should propagate exception for critical notifications")
	void propagateExceptionForCriticalNotifications() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of()
		);

		doThrow(new RuntimeException("Service unavailable")).when(notificationService).send(payload);

		assertThrows(RuntimeException.class, () -> notificationEventListener.handleCritical(payload));
	}

	@Test
	@DisplayName("Should propagate exception for transactional notifications")
	void propagateExceptionForTransactionalNotifications() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.REMINDER,
				"test@example.com",
				Map.of()
		);

		doThrow(new RuntimeException("Template not found")).when(notificationService).send(payload);

		assertThrows(RuntimeException.class, () -> notificationEventListener.handleTransactional(payload));
	}

	@Test
	@DisplayName("Should handle exception gracefully for scheduled notifications")
	void handleExceptionGracefullyForScheduledNotifications() {
		NotificationPayload payload = NotificationPayload.push(
				userId,
				NotificationType.REMINDER,
				"Title",
				Map.of()
		);

		doThrow(new RuntimeException("Firebase error")).when(notificationService).send(payload);

		assertThrows(RuntimeException.class, () -> notificationEventListener.handleScheduled(payload));
	}

	@Test
	@DisplayName("Should handle critical notification with all payload fields")
	void handleCriticalNotificationWithAllFields() {
		NotificationPayload payload = new NotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"user@example.com",
				"Welcome Subject",
				"Welcome Title",
				Map.of("firstName", "John", "lastName", "Doe")
		);

		notificationEventListener.handleCritical(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should handle transactional notification with push channel")
	void handleTransactionalNotificationWithPushChannel() {
		NotificationPayload payload = NotificationPayload.push(
				userId,
				NotificationType.REMINDER,
				"Payment Received",
				Map.of("amount", "99.99", "currency", "USD")
		);

		notificationEventListener.handleTransactional(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should handle scheduled notification with SMS channel")
	void handleScheduledNotificationWithSmsChannel() {
		NotificationPayload payload = NotificationPayload.sms(
				userId,
				NotificationType.REMINDER,
				"+1234567890",
				Map.of("appointmentTime", "2:00 PM")
		);

		notificationEventListener.handleScheduled(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should handle multiple critical notifications in sequence")
	void handleMultipleCriticalNotificationsInSequence() {
		NotificationPayload payload1 = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test1@example.com",
				Map.of()
		);

		NotificationPayload payload2 = NotificationPayload.email(
				UUID.randomUUID(),
				NotificationType.WELCOME,
				"test2@example.com",
				Map.of()
		);

		notificationEventListener.handleCritical(payload1);
		notificationEventListener.handleCritical(payload2);

		verify(notificationService, times(2)).send(any(NotificationPayload.class));
	}

	@Test
	@DisplayName("Should process transactional notification with empty variables")
	void processTransactionalNotificationWithEmptyVariables() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of()
		);

		notificationEventListener.handleTransactional(payload);

		verify(notificationService).send(payload);
	}

	@Test
	@DisplayName("Should process scheduled notification with complex variables")
	void processScheduledNotificationWithComplexVariables() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.REMINDER,
				"test@example.com",
				Map.of(
						"userName", "John Doe",
						"workoutCount", "5",
						"caloriesBurned", "2500",
						"averagePace", "6:30 min/km",
						"week", "Week of Jan 1-7"
				)
		);

		notificationEventListener.handleScheduled(payload);

		verify(notificationService).send(payload);
	}
}
