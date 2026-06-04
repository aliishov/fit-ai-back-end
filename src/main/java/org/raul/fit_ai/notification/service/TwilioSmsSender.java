package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.util.TwilioProperties;

import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TwilioSmsSender implements NotificationSender {

	TwilioProperties twilioProperties;
	SmsRateLimitService smsRateLimitService;

	@Override
	public void send(ResolvedNotificationPayload payload) {
		// Avoid logging sensitive recipient data; log only high-level event
		log.info("Sending SMS (recipient masked)");

		try {
			Message.creator(
					new PhoneNumber(payload.recipient()),
					new PhoneNumber(twilioProperties.getPhoneNumber()),
					payload.body()).create();
		} catch (ApiException e) {
			log.error("Failed to send SMS", e);
			throw new NotificationException("Failed to send SMS", e);
		}

		smsRateLimitService.validateRateLimit(payload.recipient());
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}
}
