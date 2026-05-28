package org.raul.fit_ai.fitness.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProgressReponseDTO(
		UUID id,
		UUID userId,
		BigDecimal weightKg,
		Integer heightCm,
		String notes,
		UUID planId,
		OffsetDateTime recordedAt
) {
}
