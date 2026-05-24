package org.raul.fit_ai.fitness.dto.request;

import java.time.LocalDate;

public record ScheduleWorkoutRequestDTO(
		Integer sessionsPerWeek,
		Integer durationWeeks,
		LocalDate startsAt
) {
}
