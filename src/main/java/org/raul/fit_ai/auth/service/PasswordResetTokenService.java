package org.raul.fit_ai.auth.service;

import jakarta.persistence.EntityNotFoundException;
import org.raul.fit_ai.auth.dto.request.VerifyOtpRequestDTO;
import org.raul.fit_ai.auth.dto.response.VerifyOtpResponseDTO;
import org.raul.fit_ai.auth.model.PasswordResetToken;
import org.raul.fit_ai.auth.repository.PasswordResetTokenRepository;
import org.raul.fit_ai.common.exception.InvalidOtpException;
import org.raul.fit_ai.common.exception.InvalidTokenException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.time.OffsetDateTime;

import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordResetTokenService {

	static int OTP_LENGTH = 6;
	static int OTP_EXPIRY_MINUTES = 5;

	PasswordResetTokenRepository passwordResetTokenRepository;
//	NotificationService notificationService;

	protected String generateOtp(UUID userId, String identifier) {
		log.info("Generating OTP for user [{}]", userId);

		String otp = generateRawOtp();
		String otpHash = hashOtp(otp);
		String resetToken = UUID.randomUUID().toString();

		passwordResetTokenRepository.invalidateAllByUserId(userId, OffsetDateTime.now());

		PasswordResetToken entity = PasswordResetToken.builder()
				.userId(userId)
				.otpHash(otpHash)
				.resetToken(resetToken)
				.expiresAt(OffsetDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
				.build();

		passwordResetTokenRepository.save(entity);

//		notificationService.sendOtp(identifier, otp); // TODO

		log.info("OTP sent to [{}], reset token generated for user [{}]", identifier, userId);
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

		if (!verifyOtpHash(request.rawOtp(), token.getOtpHash()))
			throw new InvalidOtpException("Invalid OTP");

		token.setVerified(true);
		passwordResetTokenRepository.save(token);

		return new VerifyOtpResponseDTO(request.resetToken(), true);
	}

	private String generateRawOtp() {
		SecureRandom random = new SecureRandom();
		int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
		return String.format("%0" + OTP_LENGTH + "d", otp);
	}

	private String hashOtp(String otp) {
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
}
