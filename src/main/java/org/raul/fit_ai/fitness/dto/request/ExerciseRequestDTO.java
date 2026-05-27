package org.raul.fit_ai.fitness.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

public record ExerciseRequestDTO(

		@NotBlank(message = "Name is required")
		@Size(
				min = 2,
				max = 50,
				message = "Name should be between 2 and 50 characters"
		)
		String name,

		@NotBlank(message = "Description is required")
		@Size(
				min = 50,
				message = "Description should be at least 50 characters"
		)
		String description,

		@NotNull
		ActivityType activityType,

		@NotNull
		MuscleGroup muscleGroup,

		@NotNull
		FitnessLevel difficulty,

		String equipmentNeeded
) {
}
