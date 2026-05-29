package org.raul.fit_ai.fitness.service.ai;

import org.raul.fit_ai.fitness.dto.ai.AiDayDTO;
import org.raul.fit_ai.fitness.dto.ai.AiExerciseDTO;
import org.raul.fit_ai.fitness.dto.ai.AiWeekDTO;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.WorkoutDay;
import org.raul.fit_ai.fitness.model.WorkoutDayExercise;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.WorkoutWeek;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;
import org.raul.fit_ai.fitness.repository.WorkoutDayExerciseRepository;
import org.raul.fit_ai.fitness.repository.WorkoutDayRepository;
import org.raul.fit_ai.fitness.repository.WorkoutWeekRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutPlanBuilder {

	WorkoutWeekRepository weekRepository;
	WorkoutDayRepository dayRepository;
	WorkoutDayExerciseRepository dayExerciseRepository;
	ExerciseRepository exerciseRepository;

	@Transactional
	public void buildAndSave(WorkoutPlan plan, AiWorkoutPlanDTO aiPlan,
	                         Set<Long> allowedExerciseIds) {
		plan.setAiNotes(aiPlan.aiNotes());

		for (AiWeekDTO aiWeek : aiPlan.weeks()) {
			WorkoutWeek week = WorkoutWeek.builder()
					.workoutPlan(plan)
					.weekNumber(aiWeek.weekNumber())
					.build();
			weekRepository.save(week);

			for (AiDayDTO aiDay : aiWeek.days()) {
				WorkoutDay day = WorkoutDay.builder()
						.workoutWeek(week)
						.dayNumber(aiDay.dayNumber())
						.focus(parseMuscleGroup(aiDay.focus()))
						.notes(aiDay.notes())
						.build();
				dayRepository.save(day);

				List<AiExerciseDTO> validExercises = aiDay.exercises().stream()
						.filter(e -> allowedExerciseIds.contains(e.exerciseId()))
						.toList();

				if (validExercises.isEmpty()) {
					log.warn("No valid exercises for planId=[{}] week=[{}] day=[{}] — marking NEEDS_REVIEW",
							plan.getId(), aiWeek.weekNumber(), aiDay.dayNumber());
					plan.setStatus(PlanStatus.NEEDS_REVIEW);
				}

				for (AiExerciseDTO aiExercise : validExercises) {
					Exercise exercise = exerciseRepository
							.findById(aiExercise.exerciseId())
							.orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

					WorkoutDayExercise dayExercise = WorkoutDayExercise.builder()
							.workoutDay(day)
							.exercise(exercise)
							.sets(aiExercise.sets())
							.reps(aiExercise.reps())
							.durationSeconds(aiExercise.durationSeconds())
							.restSeconds(aiExercise.restSeconds())
							.orderIndex(aiExercise.orderIndex())
							.notes(aiExercise.notes())
							.build();
					dayExerciseRepository.save(dayExercise);
				}
			}
		}
	}

	private MuscleGroup parseMuscleGroup(String focus) {
		try {
			return MuscleGroup.valueOf(focus.toUpperCase());
		} catch (IllegalArgumentException e) {
			log.warn("Unknown muscle group [{}] from AI — defaulting to FULL_BODY", focus);
			return MuscleGroup.FULL_BODY;
		}
	}
}
