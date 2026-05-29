package org.raul.fit_ai.fitness.dto.request;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

		@NotNull(message = "Activity type is required")
		ActivityType activityType,

		@NotNull(message = "Muscle group is required")
		MuscleGroup muscleGroup,

		@NotNull(message = "Difficulty is required")
		FitnessLevel difficulty,

		@Size(
				max = 255,
				message = "Equipment needed should not exceed 255 characters"
		)
		String equipmentNeeded
) {
}
