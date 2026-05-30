package org.raul.fit_ai.fitness.service.ai;

import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.UserProgress;

import java.util.List;

public interface WorkoutAiService {

	AiWorkoutPlanDTO generatePlan(
			UserProfile profile,
			List<Exercise> exercises,
			List<UserProgress> progressHistory,
			Integer durationWeeks
	);
}
