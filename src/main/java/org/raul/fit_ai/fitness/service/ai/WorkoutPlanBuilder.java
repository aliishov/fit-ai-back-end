package org.raul.fit_ai.fitness.service.ai;

import org.raul.fit_ai.fitness.dto.ai.AiDayDTO;
import org.raul.fit_ai.fitness.dto.ai.AiExerciseDTO;
import org.raul.fit_ai.fitness.dto.ai.AiWeekDTO;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.mapper.WorkoutDayExerciseMapper;
import org.raul.fit_ai.fitness.mapper.WorkoutDayMapper;
import org.raul.fit_ai.fitness.mapper.WorkoutWeekMapper;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.WorkoutDay;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.WorkoutWeek;
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
			WorkoutWeek week = WorkoutWeekMapper.toEntity(plan, aiWeek);
			weekRepository.save(week);

			for (AiDayDTO aiDay : aiWeek.days()) {
				WorkoutDay day = WorkoutDayMapper.toEntity(week, aiDay);
				dayRepository.save(day);

				List<AiExerciseDTO> validExercises = aiDay.exercises().stream()
						.filter(e -> allowedExerciseIds.contains(e.exerciseId()))
						.toList();

				if (validExercises.isEmpty()) {
					log.warn("No valid exercises for week=[{}] day=[{}] — marking NEEDS_REVIEW",
							aiWeek.weekNumber(), aiDay.dayNumber());
					plan.setStatus(PlanStatus.NEEDS_REVIEW);
				}

				for (AiExerciseDTO aiExercise : validExercises) {
					Exercise exercise = exerciseRepository
							.findById(aiExercise.exerciseId())
							.orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

					dayExerciseRepository.save(WorkoutDayExerciseMapper.toEntity(day, exercise, aiExercise));
				}
			}
		}
	}
}
