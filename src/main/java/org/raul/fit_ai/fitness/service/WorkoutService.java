package org.raul.fit_ai.fitness.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.client.AppUserClient;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.response.InitResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanIdResponseDTO;
import org.raul.fit_ai.fitness.dto.response.ProfileIdResponseDTO;
import org.raul.fit_ai.fitness.mapper.WorkoutPlanMapper;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutService {

	UserProfileService userProfileService;
	WorkoutPlanRepository workoutPlanRepository;
	WorkoutPlanMapper workoutPlanMapper;
	WorkoutPlanGenerationService workoutPlanGenerationService;

	public InitResponseDTO initWorkout(UserPrincipal principal) {
		log.info("Checking workout init for user [{}]", principal.getId());

		boolean hasProfile = userProfileService.existsByUserId(principal.getId());
		boolean hasActivePlan = workoutPlanRepository.existsByUserIdAndStatus(principal.getId(), PlanStatus.ACTIVE);

		return new InitResponseDTO(hasProfile, hasActivePlan);
	}

	@Transactional
	public PlanIdResponseDTO generateWorkout(UserPrincipal principal, ProfileRequestDTO request) {
		log.info("Generating workout plan for user [{}]", principal.getId());

		userProfileService.createOrUpdateProfile(principal.getId(), request);

		WorkoutPlan plan = workoutPlanMapper.toEntity(principal.getId(), request);

		plan = workoutPlanRepository.save(plan);

		workoutPlanGenerationService.generateAsync(plan.getId(), principal.getId(), request);

		log.info("Plan generation started planId=[{}] userId=[{}]", plan.getId(), principal.getId());
		return new PlanIdResponseDTO(plan.getId());
	}

	protected boolean existsById(UUID planId) {
		return workoutPlanRepository.existsById(planId);
	}

	protected boolean isPLanActive(UUID planId) {
		return workoutPlanRepository.existsByIdAndStatus(planId, PlanStatus.ACTIVE);
	}
}
