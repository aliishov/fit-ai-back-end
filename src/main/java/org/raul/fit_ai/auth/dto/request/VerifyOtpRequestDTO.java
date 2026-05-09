package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidOtp;
import org.raul.fit_ai.auth.annotation.ValidResetToken;

public record VerifyOtpRequestDTO(

		@ValidResetToken
		String resetToken,

		@ValidOtp
		String rawOtp
) {
}
