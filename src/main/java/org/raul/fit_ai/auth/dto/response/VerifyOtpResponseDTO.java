package org.raul.fit_ai.auth.dto.response;

public record VerifyOtpResponseDTO(
		String resetToken,
		boolean verified
) {
}
