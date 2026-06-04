package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.OtpToken;
import org.raul.fit_ai.auth.model.enumerated.OtpType;
import org.raul.fit_ai.auth.repository.OtpTokenRepository;
import org.raul.fit_ai.common.exception.InvalidOtpException;
import org.raul.fit_ai.common.exception.InvalidTokenException;
import org.raul.fit_ai.common.services.NotificationPublisher;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OtpService {

	static int OTP_LENGTH = 6;
	protected static int OTP_EXPIRY_MINUTES = 5;

	OtpTokenRepository otpTokenRepository;
	NotificationPublisher notificationPublisher;

	@Transactional
	public void generateOtp(BaseUser user, OtpType type, String destination) {
		String otp = generateRawOtp();
		String otpHash = hashOtp(otp);

		otpTokenRepository.invalidateAllByUserIdAndType(user.getId(), type, OffsetDateTime.now());

		otpTokenRepository.save(OtpToken.builder()
				.userId(user.getId())
				.type(type)
				.otpHash(otpHash)
				.expiresAt(OffsetDateTime.now().plusMinutes(OtpService.OTP_EXPIRY_MINUTES))
				.build());

		sendNotification(user, type, otp, destination);
		log.info("OTP notification requested type=[{}]", type);
	}

	@Transactional
	public boolean verifyOtp(UUID userId, OtpType type, String rawOtp) {
		OtpToken token = otpTokenRepository
				.findByUserIdAndTypeAndUsedAtIsNull(userId, type)
				.orElseThrow(() -> new InvalidOtpException("Invalid OTP"));

		validate(token.isExpired(), token.isUsed(), token.isVerified(),
				rawOtp, token.getOtpHash());

		token.setVerified(true);
		token.setUsedAt(OffsetDateTime.now());
		otpTokenRepository.save(token);

		return true;
	}

	protected String generateRawOtp() {
		SecureRandom random = new SecureRandom();
		int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
		return String.format("%0" + OTP_LENGTH + "d", otp);
	}

	protected String hashOtp(String otp) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 not available", e);
		}
	}

	private boolean verifyOtpHash(String rawOtp, String storedHash) {
		return hashOtp(rawOtp).equals(storedHash);
	}

	public void validate(
			boolean isExpired, boolean isUsed,
			boolean isVerified, String rawOtp, String storedHash
	) {
		if (isExpired) {
			throw new InvalidTokenException("Token expired");
		}

		if (isUsed) {
			throw new InvalidTokenException("Token already used");
		}

		if (isVerified) {
			throw new InvalidTokenException("Token already verified");
		}

		if (!verifyOtpHash(rawOtp, storedHash)) {
			throw new InvalidOtpException("Invalid OTP");
		}
	}

	private void sendNotification(BaseUser user, OtpType type,
	                              String otp, String destination) {
		NotificationType notificationType = switch (type) {
			case EMAIL_VERIFICATION -> NotificationType.EMAIL_VERIFICATION;
			case PHONE_VERIFICATION -> NotificationType.PHONE_VERIFICATION;
		};

		NotificationPayload payload = destination.startsWith("+")
				? NotificationPayload.sms(user.getId(), notificationType, destination,
				Map.of("code", otp, "minutes", "5"))
				: NotificationPayload.email(user.getId(), notificationType, destination,
				Map.of("code", otp, "minutes", "5"));

		notificationPublisher.publishCritical(payload);
	}
}
