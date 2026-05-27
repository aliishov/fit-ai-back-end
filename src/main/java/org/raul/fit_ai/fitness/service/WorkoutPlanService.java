package org.raul.fit_ai.fitness.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutPlanService {

	WorkoutPlanRepository workoutPlanRepository;

	public boolean hasActivePlan(UUID userId) {
		return workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.ACTIVE);
	}
}
