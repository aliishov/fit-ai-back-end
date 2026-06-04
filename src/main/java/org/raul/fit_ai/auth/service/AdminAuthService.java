package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.mapper.AdminUserMapper;
import org.raul.fit_ai.auth.model.AdminUser;
import org.raul.fit_ai.auth.repository.AdminUserRepository;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.common.exception.UnauthorizedException;
import org.raul.fit_ai.common.services.NotificationPublisher;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminAuthService extends BaseAuthService<AdminUser, AdminUserRepository> {

	AdminUserMapper adminUserMapper;

	public AdminAuthService(
			AdminUserRepository adminRepository,
			AdminUserMapper adminUserMapper,
			@Qualifier("adminAuthenticationProvider") AuthenticationProvider authenticationProvider,
			JwtManager jwtManager,
			PasswordResetTokenService passwordResetTokenService,
			PasswordManagementService passwordManagementService,
			NotificationPublisher notificationPublisher
	) {
		super(adminRepository, authenticationProvider, jwtManager,
				passwordResetTokenService, passwordManagementService, notificationPublisher);
		this.adminUserMapper = adminUserMapper;
	}

	@Transactional
	public URI createAdmin(UUID adminId, RegisterRequestDTO request) {
		log.info("Admin account creation requested");

		if (!userRepository.existsByIdAndEnabledTrue(adminId)) {
			throw new UnauthorizedException("Only active admins can create new admin accounts");
		}

		if (userRepository.existsByEmail(request.email())) {
			throw new DuplicateResourceException("An account with this email already exists");
		}

		AdminUser saved = userRepository.save(adminUserMapper.toEntity(adminId, request));

		pushNotification(saved, NotificationType.WELCOME);

		log.info("Admin account created");
		return URI.create("/api/v1/admin/auth/admins/" + saved.getId());
	}

	@Override
	protected Optional<AdminUser> findByIdentifier(String identifier) {
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
