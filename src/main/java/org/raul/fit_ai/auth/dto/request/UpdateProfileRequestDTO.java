package org.raul.fit_ai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.raul.fit_ai.auth.annotation.ValidPhone;
import org.raul.fit_ai.auth.util.ValidationPatterns;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateProfileRequestDTO(

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

		@ValidPhone
		String phone
) {
}
