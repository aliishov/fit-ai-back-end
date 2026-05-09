package org.raul.fit_ai.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.raul.fit_ai.auth.annotation.ValidEmailOrPhone;

public record IdentifierRequestDTO(

		@NotBlank(message = "Identifier is required")
		@Size(
				max = 320,
				message = "Identifier must not exceed 320 characters"
		)
		@ValidEmailOrPhone(message = "Identifier must be a valid email address or phone number")
		String identifier
) {
}
