package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.exception.BadRequestException;
import org.raul.fit_ai.common.exception.UnauthorizedException;
import org.raul.fit_ai.fitness.dto.request.GenerateWorkoutRequestDTO;
import org.raul.fit_ai.fitness.dto.response.InitResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanIdResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanResponseDTO;
import org.raul.fit_ai.fitness.mapper.WorkoutPlanMapper;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class WorkoutService {

	private static final Set<PlanStatus> OPEN_PLAN_STATUSES = EnumSet.of(
			PlanStatus.GENERATING,
			PlanStatus.ACTIVE,
			PlanStatus.NEEDS_REVIEW
	);

	UserProfileService userProfileService;
	WorkoutPlanRepository workoutPlanRepository;
	WorkoutPlanGenerationService workoutPlanGenerationService;

	public InitResponseDTO initWorkout(UserPrincipal principal) {
		UUID userId = requireUserId(principal);
		log.info("Checking workout init for user [{}]", userId);

		boolean hasProfile = userProfileService.existsByUserId(userId);
		boolean profileComplete = userProfileService.hasCompleteProfile(userId);
		boolean hasActivePlan = workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.ACTIVE);
		boolean hasGeneratingPlan = workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.GENERATING);
		boolean hasPlanNeedingReview = workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.NEEDS_REVIEW);
		boolean canGeneratePlan = profileComplete
				&& !hasActivePlan
				&& !hasGeneratingPlan
				&& !hasPlanNeedingReview;

		return new InitResponseDTO(
				hasProfile,
				profileComplete,
				hasActivePlan,
				hasGeneratingPlan,
				hasPlanNeedingReview,
				canGeneratePlan
		);
	}

	@Transactional
	public PlanIdResponseDTO generateWorkout(UserPrincipal principal, GenerateWorkoutRequestDTO request) {
		UUID userId = requireUserId(principal);
		validateGenerateRequest(request);
		log.info("Generating workout plan for user [{}]", userId);

		if (workoutPlanRepository.existsByUserIdAndStatusIn(userId, OPEN_PLAN_STATUSES)) {
			throw new BadRequestException("User already has an active or pending workout plan");
		}

		UserProfile profile = userProfileService.findCompleteByUserId(userId);
		WorkoutPlan plan = WorkoutPlanMapper.toEntity(userId, profile, request);

		plan = workoutPlanRepository.save(plan);

		workoutPlanGenerationService.generateAsync(plan.getId(), userId);

		log.info("Plan generation started planId=[{}] userId=[{}]", plan.getId(), userId);
		return new PlanIdResponseDTO(plan.getId());
	}

	public PlanResponseDTO getPlan(UserPrincipal principal, UUID planId) {
		UUID userId = requireUserId(principal);
		log.info("Getting plan [{}] for user [{}]", planId, userId);

		WorkoutPlan plan = workoutPlanRepository.findByIdAndUserId(planId, userId)
				.orElseThrow(() -> new EntityNotFoundException("Workout plan not found"));

		return WorkoutPlanMapper.toResponseDto(plan);
	}

	private UUID requireUserId(UserPrincipal principal) {
		if (principal == null || principal.getId() == null) {
			throw new UnauthorizedException("Authenticated user is required");
		}
		return principal.getId();
	}

	private void validateGenerateRequest(GenerateWorkoutRequestDTO request) {
		if (request == null) {
			throw new BadRequestException("Workout generation request is required");
		}
	}
}
