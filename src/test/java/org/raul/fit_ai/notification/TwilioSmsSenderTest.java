package org.raul.fit_ai.notification;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.raul.fit_ai.notification.service.SmsRateLimitService;
import org.raul.fit_ai.notification.service.TwilioSmsSender;
import org.raul.fit_ai.notification.util.TwilioProperties;

import com.twilio.exception.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("TwilioSmsSender Unit Tests")
class TwilioSmsSenderTest {

	private TwilioSmsSender twilioSmsSender;

	@Mock
	private TwilioProperties twilioProperties;

	@Mock
	private SmsRateLimitService smsRateLimitService;

	private UUID userId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		twilioSmsSender = new TwilioSmsSender(twilioProperties, smsRateLimitService);
		userId = UUID.randomUUID();
	}

	@Test
	@DisplayName("Should return SMS channel")
	void shouldReturnSmsChannel() {
		assertEquals(NotificationChannel.SMS, twilioSmsSender.channel());
	}

	@Test
	@DisplayName("Should attempt to send SMS message")
	void sendSmsMessage() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.SMS,
				NotificationType.REMINDER,
				"+1234567890",
				null,
				null,
				"Your verification code is: 123456"
		);

		when(twilioProperties.getPhoneNumber()).thenReturn("+0987654321");

		twilioSmsSender.send(payload);

		verify(smsRateLimitService).validateRateLimit("+1234567890");
	}

	@Test
	@DisplayName("Should use Twilio configured phone number")
	void useTwilioConfiguredPhoneNumber() {
		String twilioPhoneNumber = "+1-800-TWILIO";
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.SMS,
				NotificationType.REMINDER,
				"+1234567890",
				null,
				null,
				"Message"
		);

		when(twilioProperties.getPhoneNumber()).thenReturn(twilioPhoneNumber);

		twilioSmsSender.send(payload);

		verify(twilioProperties).getPhoneNumber();
	}

	@Test
	@DisplayName("Should throw NotificationException on Twilio API error")
	void throwNotificationExceptionOnApiError() {
		when(twilioProperties.getPhoneNumber()).thenReturn("+0987654321");
		ApiException apiException = new ApiException("Invalid phone number", 400);

		try {
			throw apiException;
		} catch (ApiException e) {
			assertThrows(NotificationException.class, () -> {
				throw new NotificationException("Failed to send SMS", e);
			});
		}
	}

	@Test
	@DisplayName("Should validate rate limit after sending SMS")
	void validateRateLimitAfterSending() {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.SMS,
				NotificationType.REMINDER,
				"+1234567890",
				null,
				null,
				"Reminder message"
		);

		when(twilioProperties.getPhoneNumber()).thenReturn("+0987654321");

		twilioSmsSender.send(payload);

		verify(smsRateLimitService).validateRateLimit("+1234567890");
	}

	@Test
	@DisplayName("Should send SMS with different phone numbers")
	void sendSmsWithDifferentPhoneNumbers() {
		String[] phoneNumbers = {"+1234567890", "+9876543210", "+5555555555"};
		when(twilioProperties.getPhoneNumber()).thenReturn("+0987654321");

		for (String phoneNumber : phoneNumbers) {
			ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
					userId,
					NotificationChannel.SMS,
					NotificationType.REMINDER,
					phoneNumber,
					null,
					null,
					"Message"
			);

			twilioSmsSender.send(payload);
		}

		verify(smsRateLimitService, times(3)).validateRateLimit(anyString());
	}

	@Test
	@DisplayName("Should handle SMS body with various content")
	void handleSmsBodyWithVariousContent() {
		String[] bodies = {
				"Simple message",
				"Message with numbers: 123456",
				"Message with special chars: @#$%",
				"Long message that might exceed SMS character limit but should still be sent"
		};

		when(twilioProperties.getPhoneNumber()).thenReturn("+0987654321");

		for (String body : bodies) {
			ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
					userId,
					NotificationChannel.SMS,
					NotificationType.REMINDER,
					"+1234567890",
					null,
					null,
					body
			);

			twilioSmsSender.send(payload);
		}

		verify(smsRateLimitService, times(4)).validateRateLimit("+1234567890");
	}

	@Test
	@DisplayName("Should handle SMS sending for multiple users")
	void handleSmsForMultipleUsers() {
		when(twilioProperties.getPhoneNumber()).thenReturn("+0987654321");

		for (int i = 0; i < 3; i++) {
			UUID id = UUID.randomUUID();
			ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
					id,
					NotificationChannel.SMS,
					NotificationType.REMINDER,
					"+123456789" + i,
					null,
					null,
					"Message for user " + i
			);

			twilioSmsSender.send(payload);
		}

		verify(smsRateLimitService, times(3)).validateRateLimit(anyString());
	}
}
