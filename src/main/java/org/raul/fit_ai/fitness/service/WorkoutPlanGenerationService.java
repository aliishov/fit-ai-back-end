package org.raul.fit_ai.fitness.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutPlanGenerationService {

	WorkoutPlanRepository workoutPlanRepository;

	@Async
	@Transactional
	public void generateAsync(UUID planId, UUID userId, ProfileRequestDTO request) {
		try {
			log.info("Starting AI generation for planId=[{}]", planId);

			// TODO
			// String aiResponse = aiService.generatePlan(request);
			// buildAndSave(planId, aiResponse);

			workoutPlanRepository.updateStatus(planId, PlanStatus.ACTIVE);

			// TODO send push notification

			log.info("Plan generation completed planId=[{}]", planId);

		} catch (Exception e) {
			log.error("Plan generation failed planId=[{}]", planId, e);
			workoutPlanRepository.updateStatus(planId, PlanStatus.CANCELLED);
		}
	}
}
