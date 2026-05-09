package org.raul.fit_ai.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(

		@NotBlank(message = "Refresh token should not be empty")
		String refreshToken
) {
}
