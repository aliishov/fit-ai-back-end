package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.ai.AiExerciseDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.WorkoutDay;
import org.raul.fit_ai.fitness.model.WorkoutDayExercise;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkoutDayExerciseMapper {

	public static WorkoutDayExercise toEntity(WorkoutDay day, Exercise exercise, AiExerciseDTO aiExercise) {
		return WorkoutDayExercise.builder()
				.workoutDay(day)
				.exercise(exercise)
				.sets(aiExercise.sets())
				.reps(aiExercise.reps())
				.durationSeconds(aiExercise.durationSeconds())
				.restSeconds(aiExercise.restSeconds())
				.orderIndex(aiExercise.orderIndex())
				.notes(aiExercise.notes())
				.build();
	}
}
