package org.raul.fit_ai.fitness.service;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.client.AppUserClient;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
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

	AppUserClient appUserClient;
	UserProfileService userProfileService;

	public boolean initWorkout(UserPrincipal principal) {
		log.info("Checking workout for principal [{}]", principal.getId());
		return userProfileService.existsByUserId(principal.getId());
	}

	@Transactional
	public ProfileIdResponseDTO fillProfile(UserPrincipal principal, ProfileRequestDTO request) {
		log.info("Filling profile for principal [{}]", principal.getId());

		UUID profileId = userProfileService.createProfile(principal.getId(), request);
		return new ProfileIdResponseDTO(profileId);
	}
}
