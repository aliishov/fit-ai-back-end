package org.raul.fit_ai.fitness.dto.response;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProfileResponseDTO(
		UUID id,
		UUID userId,
		ActivityType activityType,
		BigDecimal weightKg,
		Integer heightCm,
		Integer age,
		Gender gender,
		FitnessGoal goal,
		FitnessLevel fitnessLevel,
		Integer sessionsPerWeek,
		String limitations,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt
) {
}
