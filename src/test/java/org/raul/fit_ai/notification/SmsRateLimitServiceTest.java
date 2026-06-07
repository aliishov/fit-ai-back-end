package org.raul.fit_ai.notification;

import org.raul.fit_ai.common.exception.SmsLimitException;
import org.raul.fit_ai.notification.service.SmsRateLimitService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("SmsRateLimitService Unit Tests")
class SmsRateLimitServiceTest {

	private SmsRateLimitService smsRateLimitService;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		smsRateLimitService = new SmsRateLimitService(redisTemplate);
	}

	@Test
	@DisplayName("Should allow first SMS for a phone number")
	void allowFirstSmsForPhoneNumber() {
		String phoneNumber = "+1234567890";

		when(valueOperations.setIfAbsent(
				"sms_limit:" + phoneNumber,
				"1",
				Duration.ofSeconds(30)
		)).thenReturn(true);

		assertDoesNotThrow(() -> smsRateLimitService.validateRateLimit(phoneNumber));
	}

	@Test
	@DisplayName("Should reject second SMS within rate limit window")
	void rejectSecondSmsWithinRateLimitWindow() {
		String phoneNumber = "+1234567890";

		when(valueOperations.setIfAbsent(
				"sms_limit:" + phoneNumber,
				"1",
				Duration.ofSeconds(30)
		)).thenReturn(false);

		assertThrows(SmsLimitException.class, () -> smsRateLimitService.validateRateLimit(phoneNumber));
	}

	@Test
	@DisplayName("Should use correct key format")
	void useCorrectKeyFormat() {
		String phoneNumber = "+1234567890";

		when(valueOperations.setIfAbsent(any(), any(), any())).thenReturn(true);

		smsRateLimitService.validateRateLimit(phoneNumber);

		verify(valueOperations).setIfAbsent(
				"sms_limit:" + phoneNumber,
				"1",
				Duration.ofSeconds(30)
		);
	}

	@Test
	@DisplayName("Should use 30 seconds cooldown")
	void use30SecondsCooldown() {
		String phoneNumber = "+1234567890";

		when(valueOperations.setIfAbsent(any(), any(), any())).thenReturn(true);

		smsRateLimitService.validateRateLimit(phoneNumber);

		verify(valueOperations).setIfAbsent(
				anyString(),
				anyString(),
				eq(Duration.ofSeconds(30))
		);
	}

	@Test
	@DisplayName("Should handle different phone numbers independently")
	void handleDifferentPhoneNumbersIndependently() {
		String phoneNumber1 = "+1234567890";
		String phoneNumber2 = "+9876543210";

		when(valueOperations.setIfAbsent(
				"sms_limit:" + phoneNumber1,
				"1",
				Duration.ofSeconds(30)
		)).thenReturn(true);

		when(valueOperations.setIfAbsent(
				"sms_limit:" + phoneNumber2,
				"1",
				Duration.ofSeconds(30)
		)).thenReturn(true);

		assertDoesNotThrow(() -> smsRateLimitService.validateRateLimit(phoneNumber1));
		assertDoesNotThrow(() -> smsRateLimitService.validateRateLimit(phoneNumber2));

		verify(valueOperations).setIfAbsent(
				"sms_limit:" + phoneNumber1,
				"1",
				Duration.ofSeconds(30)
		);
		verify(valueOperations).setIfAbsent(
				"sms_limit:" + phoneNumber2,
				"1",
				Duration.ofSeconds(30)
		);
	}

	@Test
	@DisplayName("Should throw SmsLimitException with appropriate message")
	void throwSmsLimitExceptionWithMessage() {
		String phoneNumber = "+1234567890";

		when(valueOperations.setIfAbsent(any(), any(), any())).thenReturn(false);

		SmsLimitException exception = assertThrows(
				SmsLimitException.class,
				() -> smsRateLimitService.validateRateLimit(phoneNumber)
		);

		assertTrue(exception.getMessage().contains("30 seconds"));
	}

	@Test
	@DisplayName("Should allow SMS after rate limit window expires")
	void allowSmsAfterRateLimitWindowExpires() {
		String phoneNumber = "+1234567890";

		when(valueOperations.setIfAbsent(
				"sms_limit:" + phoneNumber,
				"1",
				Duration.ofSeconds(30)
		)).thenReturn(true).thenReturn(true);

		assertDoesNotThrow(() -> smsRateLimitService.validateRateLimit(phoneNumber));
		assertDoesNotThrow(() -> smsRateLimitService.validateRateLimit(phoneNumber));
	}

	@Test
	@DisplayName("Should handle phone numbers with different formats")
	void handlePhoneNumbersWithDifferentFormats() {
		String[] phoneNumbers = {"+1234567890", "+1 (234) 567-8900", "1234567890"};

		when(valueOperations.setIfAbsent(any(), any(), any())).thenReturn(true);

		for (String phoneNumber : phoneNumbers) {
			assertDoesNotThrow(() -> smsRateLimitService.validateRateLimit(phoneNumber));
		}
	}
}
