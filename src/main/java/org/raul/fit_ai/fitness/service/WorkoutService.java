package org.raul.fit_ai.fitness.service;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.client.AppUserClient;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.response.InitResponseDTO;
import org.raul.fit_ai.fitness.dto.response.ProfileIdResponseDTO;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutService {

	UserProfileService userProfileService;
	WorkoutPlanService workoutPlanService;

	public InitResponseDTO initWorkout(UserPrincipal principal) {
		log.info("Checking workout init for user [{}]", principal.getId());

		boolean hasProfile = userProfileService.existsByUserId(principal.getId());
		boolean hasActivePlan = workoutPlanService.hasActivePlan(principal.getId());

		return new InitResponseDTO(hasProfile, hasActivePlan);
	}

	@Transactional
	public ProfileIdResponseDTO fillProfile(UserPrincipal principal, ProfileRequestDTO request) {
		log.info("Filling profile for principal [{}]", principal.getId());

		UUID profileId = userProfileService.createOrUpdateProfile(principal.getId(), request);
		return new ProfileIdResponseDTO(profileId);
	}
}
