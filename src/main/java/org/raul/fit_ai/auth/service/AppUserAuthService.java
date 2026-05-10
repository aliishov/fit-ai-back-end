package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.mapper.AppUserMapper;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.repository.AppUserRepository;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.exception.DuplicateResourceException;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserAuthService extends BaseAuthService<AppUser, AppUserRepository> {

	AppUserMapper appUserMapper;

	public AppUserAuthService(
			AppUserRepository appUserRepository,
			AppUserMapper appUserMapper,
			@Qualifier("appAuthenticationProvider") AuthenticationProvider authenticationProvider,
			JwtManager jwtManager,
			PasswordResetTokenService passwordResetTokenService,
			PasswordManagementService passwordManagementService,
			NotificationPublisher notificationPublisher
	) {
		super(appUserRepository, authenticationProvider, jwtManager,
				passwordResetTokenService, passwordManagementService, notificationPublisher);
		this.appUserMapper = appUserMapper;
	}

	@Transactional
	public URI signUp(RegisterRequestDTO request) {
		log.info("Creating new app user account");

		if (userRepository.existsByEmail(request.email())) {
			throw new DuplicateResourceException("An account with this email already exists");
		}

		AppUser saved = userRepository.save(appUserMapper.toEntity(request));

		pushNotification(saved, NotificationType.WELCOME);

		log.info("Successfully created app user account [{}]", saved.getId());
		return URI.create("/api/v1/app/auth/users/" + saved.getId());
	}

	@Override
	protected Optional<AppUser> findByIdentifier(String identifier) {
		return userRepository.findByIdentifier(identifier);
	}

	@Override
	protected boolean existsByIdentifier(String identifier) {
		return userRepository.existsByIdentifier(identifier);
	}

	@Override
	protected UUID getIdByIdentifier(String identifier) {
		return userRepository.getIdByIdentifier(identifier);
	}
}
