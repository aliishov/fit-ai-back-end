package org.raul.fit_ai.notification;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.raul.fit_ai.notification.service.EmailNotificationSender;

import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("EmailNotificationSender Unit Tests")
class EmailNotificationSenderTest {

	private EmailNotificationSender emailSender;

	@Mock
	private JavaMailSender javaMailSender;

	@Mock
	private MimeMessage mimeMessage;

	private UUID userId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		emailSender = new EmailNotificationSender(javaMailSender);
		String fromAddress = "noreply@fitai.com";
		ReflectionTestUtils.setField(emailSender, "from", fromAddress);
		userId = UUID.randomUUID();
	}

	@Test
	@DisplayName("Should return EMAIL channel")
	void shouldReturnEmailChannel() {
		assertEquals(NotificationChannel.EMAIL, emailSender.channel());
	}

	@Test
	@DisplayName("Should send email successfully")
	void sendEmailSuccessfully() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"test@example.com",
				"Welcome to FitAi",
				null,
				"<h1>Hello</h1><p>Welcome to our platform</p>"
		);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailSender.send(payload);

		verify(javaMailSender).send(mimeMessage);
	}

	@Test
	@DisplayName("Should set email subject correctly")
	void sendEmailWithSubject() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"test@example.com",
				"Welcome Subject",
				null,
				"Email body"
		);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailSender.send(payload);

		verify(javaMailSender).createMimeMessage();
		verify(javaMailSender).send(mimeMessage);
	}

	@Test
	@DisplayName("Should set email recipient correctly")
	void sendEmailToCorrectRecipient() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"user@example.com",
				"Subject",
				null,
				"Body"
		);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailSender.send(payload);

		verify(javaMailSender).send(mimeMessage);
	}

	@Test
	@DisplayName("Should throw NotificationException on MessagingException")
	void throwNotificationExceptionOnMessagingException() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"invalid@example.com",
				"Subject",
				null,
				"Body"
		);

		when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

		assertThrows(NotificationException.class, () -> emailSender.send(payload));
	}

	@Test
	@DisplayName("Should handle null subject")
	void sendEmailWithNullSubject() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"test@example.com",
				null,
				null,
				"Body"
		);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailSender.send(payload);

		verify(javaMailSender).send(mimeMessage);
	}

	@Test
	@DisplayName("Should send HTML email")
	void sendHtmlEmail() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"test@example.com",
				"Welcome",
				null,
				"<html><body><h1>Welcome</h1></body></html>"
		);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailSender.send(payload);

		verify(javaMailSender).send(mimeMessage);
	}

	@Test
	@DisplayName("Should send email with special characters")
	void sendEmailWithSpecialCharacters() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.EMAIL,
				NotificationType.WELCOME,
				"test@example.com",
				"Special: Çhars & Symbols",
				null,
				"Body with special characters: é, à, ñ"
		);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailSender.send(payload);

		verify(javaMailSender).send(mimeMessage);
	}
}
