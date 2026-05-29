package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.common.services.NotificationPublisher;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;
import org.raul.fit_ai.fitness.repository.UserProfileRepository;
import org.raul.fit_ai.fitness.repository.UserProgressRepository;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;
import org.raul.fit_ai.fitness.service.ai.WorkoutAiService;
import org.raul.fit_ai.fitness.service.ai.WorkoutPlanBuilder;

import jakarta.persistence.EntityNotFoundException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutPlanGenerationService {

	WorkoutPlanRepository workoutPlanRepository;
	UserProfileRepository userProfileRepository;
	ExerciseRepository exerciseRepository;
	UserProgressRepository userProgressRepository;
	WorkoutPlanBuilder workoutPlanBuilder;
	NotificationPublisher notificationPublisher;
	WorkoutAiService workoutAiService;

	@Async
	@Transactional
	public void generateAsync(WorkoutPlan plan, UUID userId, Integer durationWeeks) {
		log.info("Starting async plan generation planId=[{}] userId=[{}]", plan.getId(), userId);

		try {
			UserProfile profile = userProfileRepository.findByUserId(userId)
					.orElseThrow(() -> new EntityNotFoundException("Profile not found"));

			List<Exercise> exercises = exerciseRepository
					.findByActivityTypeAndDifficulty(
							profile.getActivityType(),
							profile.getFitnessLevel());

			Set<Long> allowedIds = exercises.stream()
					.map(Exercise::getId)
					.collect(Collectors.toSet());

			if (exercises.isEmpty()) {
				log.error("No exercises available after filtering for userId=[{}]", userId);
				workoutPlanRepository.updateStatus(plan.getId(), PlanStatus.CANCELLED);
				return;
			}

			List<UserProgress> history = userProgressRepository.findByUserIdOrderByRecordedAtDesc(userId);

			AiWorkoutPlanDTO aiPlan = workoutAiService.generatePLan(profile, exercises, history, durationWeeks);

			workoutPlanBuilder.buildAndSave(plan, aiPlan, allowedIds);

			PlanStatus finalStatus = plan.getStatus() == PlanStatus.NEEDS_REVIEW
					? PlanStatus.NEEDS_REVIEW
					: PlanStatus.ACTIVE;

			workoutPlanRepository.updateStatus(plan.getId(), finalStatus);

			// 8. Уведомляем пользователя
//			notificationPublisher.publish(
//					NotificationPayload.push(
//							userId,
//							NotificationType.WORKOUT_PLAN_GENERATED,
//							Map.of("name", "")
//					)
//			);

			log.info("Plan generation completed planId=[{}] status=[{}]", plan.getId(), finalStatus);

		} catch (Exception e) {
			log.error("Plan generation failed planId=[{}]", plan.getId(), e);
			workoutPlanRepository.updateStatus(plan.getId(), PlanStatus.CANCELLED);
		}
	}
}
