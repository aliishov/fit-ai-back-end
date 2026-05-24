package org.raul.fit_ai.fitness.dto.request;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;

public record WorkoutGoalRequestDTO(
		FitnessGoal goal,
		ActivityType activityType
) {
}
