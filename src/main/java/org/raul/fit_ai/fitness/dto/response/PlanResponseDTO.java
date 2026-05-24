package org.raul.fit_ai.fitness.dto.response;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PlanResponseDTO(
		UUID id,
		UUID userId,
		ActivityType activityType,
		PlanStatus status,
		Integer durationWeeks,
		Integer sessionsPerWeek,
		LocalDate startsAt,
		LocalDate endsAt
		// TODO
) {
}
