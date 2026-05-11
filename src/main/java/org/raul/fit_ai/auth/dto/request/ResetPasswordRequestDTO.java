package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidEmailOrPhone;
import org.raul.fit_ai.auth.annotation.ValidPassword;
import org.raul.fit_ai.auth.annotation.ValidResetToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(

		@NotBlank(message = "Identifier is required")
		@Size(
				max = 320,
				message = "Identifier must not exceed 320 characters"
		)
		@ValidEmailOrPhone(message = "Identifier must be a valid email address or phone number")
		String identifier,

		@ValidResetToken
		String resetToken,

		@ValidPassword
		String newPassword,

		@ValidPassword
		String passwordRepeated
) {
}
