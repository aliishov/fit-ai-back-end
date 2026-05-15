package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidOtp;

public record EmailConfirmRequestDTO(

		@ValidOtp
		String rawOtp
) {
}
