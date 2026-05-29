package org.raul.fit_ai.fitness.dto.request;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileRequestDTO(

		ActivityType activityType,

		@DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
		@DecimalMax(value = "300.0", message = "Weight must not exceed 300 kg")
		@Digits(
				integer = 3,
				fraction = 2,
				message = "Weight must have at most 3 integer digits and 2 decimal places"
		)
		BigDecimal weightKg,

		@Min(value = 50, message = "Height must be at least 50 cm")
		@Max(value = 250, message = "Height must not exceed 250 cm")
		Integer heightCm,

		@Min(value = 10, message = "Age must be at least 10")
		@Max(value = 120, message = "Age must not exceed 120")
		Integer age,

		@NotNull(message = "Gender is required")
		Gender gender,

		@NotNull(message = "Goal is required")
		FitnessGoal goal,

		@NotNull(message = "Fitness level is required")
		FitnessLevel fitnessLevel,

		@NotNull(message = "Sessions per weak is required")
		@Min(1)
		@Max(7)
		Integer sessionsPerWeek,

		@NotNull
		@Min(1)
		@Max(52)
		Integer durationWeeks,

		@NotNull
		@FutureOrPresent
		LocalDate startsAt,

		@Size(
				max = 500,
				message = "Limitations must not exceed 500 characters"
		)
		String limitations
) {
}
