package org.raul.fit_ai.auth.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class OtpService {

	private static final int OTP_LENGTH = 6;
	protected static final int OTP_EXPIRY_MINUTES = 5;

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

	protected boolean verifyOtpHash(String rawOtp, String storedHash) {
		return hashOtp(rawOtp).equals(storedHash);
	}
}
