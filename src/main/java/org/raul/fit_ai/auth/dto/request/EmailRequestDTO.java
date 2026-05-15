package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidEmail;

public record EmailRequestDTO(

		@ValidEmail
		String email
) {
}
