package org.raul.fit_ai.fitness.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GenerateWorkoutRequestDTO(

		@NotNull(message = "Duration weeks is required")
		@Min(value = 1, message = "Duration weeks must be at least 1")
		@Max(value = 52, message = "Duration weeks must not exceed 52")
		Integer durationWeeks,

		@NotNull(message = "Start date is required")
		@FutureOrPresent(message = "Start date must be today or in the future")
		LocalDate startsAt
) {
}
