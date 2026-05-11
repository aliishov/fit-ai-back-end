package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.IdentifierRequestDTO;
import org.raul.fit_ai.auth.dto.request.RefreshTokenRequestDTO;
import org.raul.fit_ai.auth.dto.request.ResetPasswordRequestDTO;
import org.raul.fit_ai.auth.dto.request.SignInRequestDTO;
import org.raul.fit_ai.auth.dto.response.ResetTokenResponseDTO;
import org.raul.fit_ai.auth.dto.response.SignInResponseDTO;
import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.exception.UnauthorizedException;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class BaseAuthService<T extends BaseUser, R extends JpaRepository<T, UUID>> {

	R userRepository;
	AuthenticationProvider authenticationProvider;
	JwtManager jwtManager;
	PasswordResetTokenService passwordResetTokenService;
	PasswordManagementService passwordManagementService;
	NotificationPublisher notificationPublisher;

	@Transactional
	public SignInResponseDTO signIn(SignInRequestDTO request) {
		Authentication authentication = authenticationProvider.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.identifier(),
						request.password()
				)
		);

		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

		String accessToken = jwtManager.generateAccessToken(principal);
		String refreshToken = jwtManager.generateRefreshToken(principal);

		log.info("User [{}] signed in successfully", principal.getId());

		return new SignInResponseDTO(accessToken, refreshToken);
	}

	public SignInResponseDTO refreshToken(RefreshTokenRequestDTO request) {
		String oldRefreshToken = request.refreshToken();

		UUID userId = jwtManager.extractUserId(oldRefreshToken);
		if (userId == null) {
			throw new UnauthorizedException("Invalid refresh token");
		}

		T user = userRepository.findById(userId)
				.orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

		UserPrincipal principal = new UserPrincipal(user);

		if (!jwtManager.isTokenValid(oldRefreshToken, principal)) {
			throw new UnauthorizedException("Invalid refresh token");
		}

		String newAccessToken = jwtManager.generateAccessToken(principal);
		String newRefreshToken = jwtManager.rotateRefreshToken(principal, oldRefreshToken);

		log.info("User [{}] rotated tokens", principal.getId());

		return new SignInResponseDTO(newAccessToken, newRefreshToken);
	}

	public ResetTokenResponseDTO requestPasswordReset(IdentifierRequestDTO request) {
		String identifier = request.identifier();

		Optional<T> userOpt = findByIdentifier(identifier);

		if (userOpt.isEmpty()) {
			log.info("Password reset requested for non-existent identifier");
			return new ResetTokenResponseDTO(UUID.randomUUID().toString());
		}

		BaseUser user = userOpt.get();
		String resetToken = passwordResetTokenService.generateOtp(user, identifier);

		log.info("User [{}] requesting password reset", user.getId());
		return new ResetTokenResponseDTO(resetToken);
	}

	@Transactional
	public void resetPassword(ResetPasswordRequestDTO request) {
		passwordManagementService.validatePasswordMatch(
				request.newPassword(),
				request.passwordRepeated()
		);

		T user = findByIdentifier(request.identifier())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		passwordResetTokenService.verifyResetToken(request.resetToken(), user.getId());
		passwordManagementService.updatePassword(user, request.newPassword());

		userRepository.save(user);

		pushNotification(user, NotificationType.PASSWORD_CHANGED);

		log.info("Password reset successfully for user [{}]", user.getId());
	}

	protected abstract Optional<T> findByIdentifier(String identifier);

	protected abstract boolean existsByIdentifier(String identifier);

	protected abstract UUID getIdByIdentifier(String identifier);

	protected void pushNotification(T user, NotificationType type) {
		notificationPublisher.publishCritical(
				NotificationPayload.email(user.getId(), type,
						user.getEmail(), null)
		);
	}
}