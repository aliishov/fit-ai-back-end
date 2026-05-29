package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.EmailConfirmRequestDTO;
import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.mapper.AppUserMapper;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.model.enumerated.OtpType;
import org.raul.fit_ai.auth.repository.AppUserRepository;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.common.services.NotificationPublisher;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.net.URI;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserAuthService extends BaseAuthService<AppUser, AppUserRepository> {

	AppUserMapper appUserMapper;
	OtpService otpService;

	public AppUserAuthService(
			AppUserRepository appUserRepository,
			AppUserMapper appUserMapper,
			@Qualifier("appAuthenticationProvider") AuthenticationProvider authenticationProvider,
			JwtManager jwtManager,
			PasswordResetTokenService passwordResetTokenService,
			PasswordManagementService passwordManagementService,
			NotificationPublisher notificationPublisher,
			OtpService otpService
	) {
		super(appUserRepository, authenticationProvider, jwtManager,
				passwordResetTokenService, passwordManagementService, notificationPublisher);
		this.appUserMapper = appUserMapper;
		this.otpService = otpService;
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

	@Transactional
	public void sendEmailConfirmation(UserPrincipal principal) {
		log.info("Sending email confirmation to user [{}]", principal.getId());

		AppUser user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		otpService.generateOtp(user, OtpType.EMAIL_VERIFICATION, user.getEmail());
	}

	@Transactional
	public void emailConfirm(UserPrincipal principal, EmailConfirmRequestDTO request) {
		log.info("Confirming email confirmation to user [{}]", principal.getId());

		AppUser user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		if (otpService.verifyOtp(principal.getId(), OtpType.EMAIL_VERIFICATION, request.rawOtp())) {
			user.setEmailVerified(true);
			userRepository.save(user);
		}
	}

	@Transactional
	public void sendPhoneConfirmation(UserPrincipal principal) {
		log.info("Sending sms confirmation to user [{}]", principal.getId());

		AppUser user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		otpService.generateOtp(user, OtpType.PHONE_VERIFICATION, user.getPhone());
	}

	@Transactional
	public void phoneConfirm(UserPrincipal principal, EmailConfirmRequestDTO request) {
		log.info("Confirming phone confirmation to user [{}]", principal.getId());

		AppUser user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		if (otpService.verifyOtp(user.getId(), OtpType.PHONE_VERIFICATION, request.rawOtp())) {
			user.setPhoneVerified(true);
			userRepository.save(user);
		}
	}
}
