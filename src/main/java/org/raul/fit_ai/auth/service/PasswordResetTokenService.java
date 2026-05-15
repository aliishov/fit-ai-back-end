package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.VerifyOtpRequestDTO;
import org.raul.fit_ai.auth.dto.response.VerifyOtpResponseDTO;
import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.PasswordResetToken;
import org.raul.fit_ai.auth.repository.PasswordResetTokenRepository;
import org.raul.fit_ai.common.exception.InvalidOtpException;
import org.raul.fit_ai.common.exception.InvalidTokenException;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordResetTokenService {

	static int OTP_EXPIRY_MINUTES = 5;

	OtpService otpService;
	PasswordResetTokenRepository passwordResetTokenRepository;
	NotificationPublisher notificationPublisher;

	@Transactional
	protected String generateOtp(BaseUser user, String identifier) {
		log.info("Generating OTP for user [{}]", user.getId());

		String otp = otpService.generateRawOtp();
		String otpHash = otpService.hashOtp(otp);
		String resetToken = UUID.randomUUID().toString();

		passwordResetTokenRepository.invalidateAllByUserId(user.getId(), OffsetDateTime.now());

		PasswordResetToken entity = PasswordResetToken.builder()
				.userId(user.getId())
				.otpHash(otpHash)
				.resetToken(resetToken)
				.expiresAt(OffsetDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
				.build();

		passwordResetTokenRepository.save(entity);

		pushNotification(user, otp, identifier);

		log.info("OTP sent to [{}], reset token generated for user [{}]", identifier, user.getId());
		return resetToken;
	}

	public VerifyOtpResponseDTO verifyOtp(VerifyOtpRequestDTO request) {
		PasswordResetToken token = passwordResetTokenRepository.findByResetToken(request.resetToken())
				.orElseThrow(() -> new InvalidTokenException("Token not found"));

		if (token.isExpired()) {
			throw new InvalidTokenException("Token expired");
		}

		if (token.isUsed()) {
			throw new InvalidTokenException("Token already used");
		}

		if (token.isVerified()) {
			return new VerifyOtpResponseDTO(request.resetToken(), true);
		}

		if (!otpService.verifyOtpHash(request.rawOtp(), token.getOtpHash()))
			throw new InvalidOtpException("Invalid OTP");

		token.setVerified(true);
		passwordResetTokenRepository.save(token);

		return new VerifyOtpResponseDTO(request.resetToken(), true);
	}

	public void verifyResetToken(String resetToken, UUID userId) {
		PasswordResetToken token = passwordResetTokenRepository
				.findByResetToken(resetToken)
				.orElseThrow(() -> new EntityNotFoundException("Token not found"));

		if (token.isUsed()) {
			throw new InvalidTokenException("Token already used");
		}

		if (token.isExpired()) {
			throw new InvalidTokenException("Token expired");
		}

		if (!token.getUserId().equals(userId)) {
			throw new InvalidTokenException("Invalid token");
		}

		if (!token.isVerified()) {
			throw new InvalidTokenException("Token is not verified");
		}
	}

	// Push not suitable for OTP
	private void pushNotification(BaseUser user, String otp, String identifier) {
		if (identifier.startsWith("+")) {
			notificationPublisher.publishCritical(
					NotificationPayload.sms(user.getId(), NotificationType.OTP,
							identifier, Map.of("code", otp, "minutes", "5"))
			);
		} else {
			notificationPublisher.publishCritical(
					NotificationPayload.email(user.getId(), NotificationType.OTP,
							identifier, Map.of("code", otp, "minutes", "5"))
			);
		}
	}
}
