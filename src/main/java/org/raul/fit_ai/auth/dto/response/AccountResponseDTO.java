package org.raul.fit_ai.auth.dto.response;

import java.util.UUID;

public record AccountResponseDTO(
		UUID id,
		String firstName,
		String lastName,
		String email,
		String phone,
		String avatarUrl
) {
}
