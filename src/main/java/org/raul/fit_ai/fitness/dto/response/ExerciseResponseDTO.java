package org.raul.fit_ai.fitness.dto.response;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

public record ExerciseResponseDTO(
		Long id,
		String name,
		String description,
		ActivityType activityType,
		MuscleGroup muscleGroup,
		FitnessLevel difficulty,
		String equipmentNeeded
) {
}
