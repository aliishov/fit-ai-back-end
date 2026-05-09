package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidEmail;
import org.raul.fit_ai.auth.annotation.ValidPassword;
import org.raul.fit_ai.auth.util.ValidationPatterns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.ToString;

public record RegisterRequestDTO(

		@NotBlank(message = "First name is required")
		@Size(
				min = 1,
				max = 255,
				message = "First name must be between 1 and 255 characters"
		)
		@Pattern(
				regexp = ValidationPatterns.NAME,
				message = "First name must contain only letters, spaces, hyphens, or apostrophes"
		)
		String firstName,

		@NotBlank(message = "Last name is required")
		@Size(
				min = 1,
				max = 255,
				message = "Last name must be between 1 and 255 characters"
		)
		@Pattern(
				regexp = ValidationPatterns.NAME,
				message = "Last name must contain only letters, spaces, hyphens, or apostrophes"
		)
		String lastName,

		@ValidEmail
		String email,

		@ValidPassword
		@ToString.Exclude
		String password
) {
}
