package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.ai.AiWeekDTO;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.WorkoutWeek;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkoutWeekMapper {

	public static WorkoutWeek toEntity(WorkoutPlan plan, AiWeekDTO aiWeek) {
		return WorkoutWeek.builder()
				.workoutPlan(plan)
				.weekNumber(aiWeek.weekNumber())
				.build();
	}
}