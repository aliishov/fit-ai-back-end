package org.raul.fit_ai.notification;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.common.exception.TemplateNotFoundException;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.NotificationLog;
import org.raul.fit_ai.notification.model.NotificationTemplate;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationStatus;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.raul.fit_ai.notification.repository.NotificationLogRepository;
import org.raul.fit_ai.notification.repository.NotificationTemplateRepository;
import org.raul.fit_ai.notification.service.NotificationSender;
import org.raul.fit_ai.notification.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

	private NotificationService notificationService;

	@Mock
	private NotificationSender emailSender;

	@Mock
	private NotificationSender pushSender;

	@Mock
	private NotificationSender smsSender;

	@Mock
	private NotificationTemplateRepository templateRepository;

	@Mock
	private NotificationLogRepository logRepository;

	private UUID userId;
	private NotificationTemplate emailTemplate;
	private NotificationTemplate pushTemplate;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		when(emailSender.channel()).thenReturn(NotificationChannel.EMAIL);
		when(pushSender.channel()).thenReturn(NotificationChannel.PUSH);
		when(smsSender.channel()).thenReturn(NotificationChannel.SMS);

		notificationService = new NotificationService(
				List.of(emailSender, pushSender, smsSender),
				templateRepository,
				logRepository
		);

		userId = UUID.randomUUID();
		emailTemplate = NotificationTemplate.builder()
				.subject("Test Email")
				.body("Hello {name}, welcome!")
				.active(true)
				.build();

		pushTemplate = NotificationTemplate.builder()
				.subject(null)
				.body("Welcome {name}!")
				.active(true)
				.build();
	}

	@Test
	@DisplayName("Should send email notification successfully")
	void sendEmailNotificationSuccessfully() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of("name", "John")
		);

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.WELCOME,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.of(emailTemplate));

		notificationService.send(payload);

		verify(emailSender).send(argThat(p ->
				p.userId().equals(userId) &&
				p.channel() == NotificationChannel.EMAIL &&
				p.body().contains("John")
		));

		ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
		verify(logRepository).save(logCaptor.capture());

		NotificationLog log = logCaptor.getValue();
		assertEquals(NotificationStatus.SENT, log.getStatus());
		assertNull(log.getErrorMessage());
	}

	@Test
	@DisplayName("Should send push notification successfully")
	void sendPushNotificationSuccessfully() {
		NotificationPayload payload = NotificationPayload.push(
				userId,
				NotificationType.REMINDER,
				"Workout Reminder",
				Map.of("activity", "Running")
		);

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.REMINDER,
				NotificationChannel.PUSH,
				true
		)).thenReturn(Optional.of(pushTemplate));

		notificationService.send(payload);

		verify(pushSender).send(argThat(p ->
				p.userId().equals(userId) &&
				p.channel() == NotificationChannel.PUSH &&
				p.body().contains("Running")
		));

		verify(logRepository).save(any(NotificationLog.class));
	}

	@Test
	@DisplayName("Should interpolate multiple variables in notification body")
	void sendNotificationWithMultipleVariables() {
		Map<String, String> variables = Map.of(
				"name", "Alice",
				"count", "5",
				"date", "2025-01-01"
		);

		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.REMINDER,
				"alice@example.com",
				variables
		);

		NotificationTemplate template = NotificationTemplate.builder()
				.subject("Congrats {name}!")
				.body("You have completed {count} workouts by {date}")
				.active(true)
				.build();

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.REMINDER,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.of(template));

		notificationService.send(payload);

		ArgumentCaptor<ResolvedNotificationPayload> captor = ArgumentCaptor.forClass(ResolvedNotificationPayload.class);
		verify(emailSender).send(captor.capture());

		ResolvedNotificationPayload resolved = captor.getValue();
		assertEquals("Congrats Alice!", resolved.subject());
		assertEquals("You have completed 5 workouts by 2025-01-01", resolved.body());
	}

	@Test
	@DisplayName("Should throw TemplateNotFoundException when template not found")
	void sendNotificationThrowsExceptionWhenTemplateNotFound() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of()
		);

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.WELCOME,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.empty());

		assertThrows(TemplateNotFoundException.class, () -> notificationService.send(payload));
	}

	@Test
	@DisplayName("Should throw NotificationException when sender not available")
	void sendNotificationThrowsExceptionWhenSenderNotAvailable() {
		NotificationService serviceWithoutSmsSender = new NotificationService(
				List.of(emailSender, pushSender),
				templateRepository,
				logRepository
		);

		NotificationPayload payload = NotificationPayload.sms(
				userId,
				NotificationType.REMINDER,
				"+1234567890",
				Map.of()
		);

		NotificationTemplate template = NotificationTemplate.builder()
				.subject(null)
				.body("Your code is {code}")
				.active(true)
				.build();

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.REMINDER,
				NotificationChannel.SMS,
				true
		)).thenReturn(Optional.of(template));

		assertThrows(NotificationException.class, () -> serviceWithoutSmsSender.send(payload));
	}

	@Test
	@DisplayName("Should log failure when sender throws exception")
	void sendNotificationLogsFailureWhenSenderThrows() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of()
		);

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.WELCOME,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.of(emailTemplate));

		String errorMessage = "Mail server connection failed";
		doThrow(new RuntimeException(errorMessage)).when(emailSender).send(any());

		assertThrows(RuntimeException.class, () -> notificationService.send(payload));

		ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
		verify(logRepository).save(logCaptor.capture());

		NotificationLog log = logCaptor.getValue();
		assertEquals(NotificationStatus.FAILED, log.getStatus());
		assertEquals(errorMessage, log.getErrorMessage());
	}

	@Test
	@DisplayName("Should use userId as recipient when recipient not provided")
	void sendNotificationUsesUserIdAsRecipientWhenNotProvided() {
		NotificationPayload payload = NotificationPayload.push(
				userId,
				NotificationType.REMINDER,
				"Title",
				Map.of()
		);

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.REMINDER,
				NotificationChannel.PUSH,
				true
		)).thenReturn(Optional.of(pushTemplate));

		notificationService.send(payload);

		ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
		verify(logRepository).save(logCaptor.capture());

		NotificationLog log = logCaptor.getValue();
		assertEquals(userId.toString(), log.getRecipient());
	}

	@Test
	@DisplayName("Should use provided recipient")
	void sendNotificationUsesProvidedRecipient() {
		String recipient = "user@example.com";
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				recipient,
				Map.of()
		);

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.WELCOME,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.of(emailTemplate));

		notificationService.send(payload);

		ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
		verify(logRepository).save(logCaptor.capture());

		NotificationLog log = logCaptor.getValue();
		assertEquals(recipient, log.getRecipient());
	}

	@Test
	@DisplayName("Should handle empty variables map")
	void interpolateHandlesEmptyVariables() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of()
		);

		NotificationTemplate template = NotificationTemplate.builder()
				.subject("Hello")
				.body("Welcome to FitAi")
				.active(true)
				.build();

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.WELCOME,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.of(template));

		notificationService.send(payload);

		ArgumentCaptor<ResolvedNotificationPayload> captor = ArgumentCaptor.forClass(ResolvedNotificationPayload.class);
		verify(emailSender).send(captor.capture());

		ResolvedNotificationPayload resolved = captor.getValue();
		assertEquals("Welcome to FitAi", resolved.body());
	}

	@Test
	@DisplayName("Should handle null template body")
	void interpolateHandlesNullTemplate() {
		NotificationPayload payload = NotificationPayload.email(
				userId,
				NotificationType.WELCOME,
				"test@example.com",
				Map.of("name", "John")
		);

		NotificationTemplate template = NotificationTemplate.builder()
				.subject("Hello")
				.body(null)
				.active(true)
				.build();

		when(templateRepository.findByTypeAndChannelAndActive(
				NotificationType.WELCOME,
				NotificationChannel.EMAIL,
				true
		)).thenReturn(Optional.of(template));

		notificationService.send(payload);

		ArgumentCaptor<ResolvedNotificationPayload> captor = ArgumentCaptor.forClass(ResolvedNotificationPayload.class);
		verify(emailSender).send(captor.capture());

		ResolvedNotificationPayload resolved = captor.getValue();
		assertNull(resolved.body());
	}
}
