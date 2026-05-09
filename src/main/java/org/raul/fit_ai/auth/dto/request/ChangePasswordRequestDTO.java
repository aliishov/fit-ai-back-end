package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.annotation.ValidPassword;

public record ChangePasswordRequestDTO(
		@ValidPassword
		String oldPassword,

		@ValidPassword
		String newPassword,

		String refreshToken
) {
}
