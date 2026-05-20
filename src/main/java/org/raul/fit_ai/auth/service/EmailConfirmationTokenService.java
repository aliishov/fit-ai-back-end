package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.EmailConfirmRequestDTO;
import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.EmailVerificationToken;
import org.raul.fit_ai.auth.repository.EmailConfirmationTokenRepository;
import org.raul.fit_ai.common.exception.InvalidOtpException;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static org.raul.fit_ai.auth.service.OtpService.OTP_EXPIRY_MINUTES;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailConfirmationTokenService {

	EmailConfirmationTokenRepository emailConfirmationTokenRepository;
	NotificationPublisher notificationPublisher;
	OtpService otpService;

	@Transactional
	protected void generateEmailConfirmationToken(BaseUser user, String identifier) {
		log.info("Generating OTP for user [{}]", user.getId());

		String otp = otpService.generateRawOtp();
		String otpHash = otpService.hashOtp(otp);

		emailConfirmationTokenRepository.invalidateAllByUserId(user.getId(), OffsetDateTime.now());

		EmailVerificationToken entity = EmailVerificationToken.builder()
				.userId(user.getId())
				.otpHash(otpHash)
				.expiresAt(OffsetDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
				.build();

		emailConfirmationTokenRepository.save(entity);

		pushNotification(user, otp, identifier);

		log.info("OTP sent to [{}], confirmation token generated for user [{}]", identifier, user.getId());
	}

	private void pushNotification(BaseUser user, String otp, String identifier) {
		notificationPublisher.publishCritical(
				NotificationPayload.email(user.getId(), NotificationType.EMAIL_VERIFICATION,
						identifier, Map.of("code", otp, "minutes", "5"))
		);
	}

	public boolean confirm(UUID userId, EmailConfirmRequestDTO request) {
		return verifyOtp(userId, request.rawOtp());
	}

	private boolean verifyOtp(UUID userId, String otpRaw) {
		EmailVerificationToken token = emailConfirmationTokenRepository
				.findByUserIdAndUsedAtIsNull(userId)
				.orElseThrow(() -> new InvalidOtpException("Invalid OTP"));

		otpService.validate(
				token.isExpired(), token.isUsed(), token.isVerified(),
				otpRaw, token.getOtpHash()
		);

		token.setVerified(true);
		token.setUsedAt(OffsetDateTime.now());
		emailConfirmationTokenRepository.save(token);

		return true;
	}
}
