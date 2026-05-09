package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidEmailOrPhone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequestDTO(

		@NotBlank(message = "Identifier is required")
		@Size(
				max = 320,
				message = "Identifier must not exceed 320 characters"
		)
		@ValidEmailOrPhone(message = "Identifier must be a valid email address or phone number")
		String identifier,

		@NotBlank(message = "Password is required")
		@Size(
				min = 8,
				max = 128,
				message = "Password must be between 8 and 128 characters"
		)
		String password
) {
}
