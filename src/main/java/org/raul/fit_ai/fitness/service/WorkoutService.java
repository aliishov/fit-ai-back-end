package org.raul.fit_ai.fitness.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.client.AppUserClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutService {

	AppUserClient appUserClient;
	UserProfileService userProfileService;

	public boolean initWorkout(UserPrincipal principal) {
		return userProfileService.existsByUserId(principal.getId());
	}
}
