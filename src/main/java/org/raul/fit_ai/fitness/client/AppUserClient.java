package org.raul.fit_ai.fitness.client;

import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.service.AppUserAuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppUserClient {

	AppUserAuthService appUserAuthService;

	public Optional<AppUser> findById(UUID userId) {
		return appUserAuthService.findById(userId);
	}
}
