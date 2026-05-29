package org.raul.fit_ai.fitness.dto.request;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

import jakarta.validation.constraints.Size;

public record ExerciseUpdateRequestDTO(

		@Size(
				min = 2,
				max = 50,
				message = "Name should be between 2 and 50 characters"
		)
		String name,

		@Size(
				min = 50,
				message = "Description should be at least 50 characters"
		)
		String description,

		ActivityType activityType,
		MuscleGroup muscleGroup,
		FitnessLevel difficulty,
		String equipmentNeeded
) {
}
