package org.raul.fit_ai.auth.controller;

import org.raul.fit_ai.auth.annotation.AppUser;
import org.raul.fit_ai.auth.dto.request.EmailRequestDTO;
import org.raul.fit_ai.auth.dto.request.IdentifierRequestDTO;
import org.raul.fit_ai.auth.dto.request.RefreshTokenRequestDTO;
import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.dto.request.ResetPasswordRequestDTO;
import org.raul.fit_ai.auth.dto.request.SignInRequestDTO;
import org.raul.fit_ai.auth.dto.response.ResetTokenResponseDTO;
import org.raul.fit_ai.auth.dto.response.SignInResponseDTO;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.service.AppUserAuthService;
import org.raul.fit_ai.common.dto.BaseResponseDTO;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/app/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class AppAuthController {

	AppUserAuthService appUserAuthService;

	@PostMapping("/users")
	public ResponseEntity<BaseResponseDTO<Void>> signUp(
			@RequestBody @Valid RegisterRequestDTO request
	) {
		URI location = appUserAuthService.signUp(request);
		return ResponseEntity
				.created(location)
				.body(BaseResponseDTO.success("User account created successfully"));
	}

	// POST /api/v1/app/auth/sessions
	@PostMapping("/sessions")
	public ResponseEntity<BaseResponseDTO<SignInResponseDTO>> signIn(
			@RequestBody @Valid SignInRequestDTO request
	) {
		SignInResponseDTO data = appUserAuthService.signIn(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Signed in successfully"));
	}

	// POST /api/v1/app/auth/passwords/reset-request
	@PostMapping("/passwords/reset-request")
	public ResponseEntity<BaseResponseDTO<ResetTokenResponseDTO>> requestPasswordReset(
			@RequestBody @Valid IdentifierRequestDTO request
	) {
		ResetTokenResponseDTO data = appUserAuthService.requestPasswordReset(request);
		return ResponseEntity
				.accepted()
				.body(BaseResponseDTO.success(data, "Password reset instructions sent if the account exists"));
	}

	// POST /api/v1/app/auth/passwords/reset
	@PostMapping("/passwords/reset")
	public ResponseEntity<BaseResponseDTO<Void>> resetPassword(
			@RequestBody @Valid ResetPasswordRequestDTO request
	) {
		appUserAuthService.resetPassword(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success("Password reset successfully"));
	}

	// POST /api/v1/app/auth/sessions/refresh
	@AppUser
	@PostMapping("/sessions/refresh")
	public ResponseEntity<BaseResponseDTO<SignInResponseDTO>> refreshToken(
			@RequestBody @Valid RefreshTokenRequestDTO request
	) {
		SignInResponseDTO data = appUserAuthService.refreshToken(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Token refreshed successfully"));
	}

	@AppUser
	@PostMapping("/email/send-confirmation")
	public ResponseEntity<BaseResponseDTO<Void>> sendEmailConfirmation(
			@AuthenticationPrincipal UserPrincipal user,
			@RequestBody @Valid EmailRequestDTO request
	) {
		appUserAuthService.sendEmailConfirmation(user.getId(), request);
		return ResponseEntity
				.ok(BaseResponseDTO.success("Email send successfully"));
	}
}
