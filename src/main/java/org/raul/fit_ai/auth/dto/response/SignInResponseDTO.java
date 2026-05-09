package org.raul.fit_ai.auth.dto.response;

public record SignInResponseDTO(
		String accessToken,
		String refreshToken
) {
}
