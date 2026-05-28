package org.raul.fit_ai.fitness.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecordProgressRequestDTO(

		@NotNull(message = "Weight is required")
		@DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
		@DecimalMax(value = "300.0", message = "Weight must not exceed 300 kg")
		@Digits(
				integer = 3,
				fraction = 2,
				message = "Weight must have at most 3 integer digits and 2 decimal places"
		)
		BigDecimal weightKg,

		@NotNull(message = "Height is required")
		@Min(value = 50, message = "Height must be at least 50 cm")
		@Max(value = 250, message = "Height must not exceed 250 cm")
		Integer heightCm,

		@Size(
				max = 1000,
				message = "Notes must not exceed 1000 characters"
		)
		String notes,

		@NotNull(message = "Plan ID is required")
		UUID planId

) {
}
