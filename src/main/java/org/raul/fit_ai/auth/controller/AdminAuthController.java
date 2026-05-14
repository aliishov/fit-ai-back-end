package org.raul.fit_ai.auth.controller;

import org.raul.fit_ai.auth.annotation.AdminUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.service.AdminAuthService;
import org.raul.fit_ai.auth.dto.request.IdentifierRequestDTO;
import org.raul.fit_ai.auth.dto.request.RefreshTokenRequestDTO;
import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.dto.request.ResetPasswordRequestDTO;
import org.raul.fit_ai.auth.dto.request.SignInRequestDTO;
import org.raul.fit_ai.auth.dto.response.ResetTokenResponseDTO;
import org.raul.fit_ai.auth.dto.response.SignInResponseDTO;
import org.raul.fit_ai.common.dto.BaseResponseDTO;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.net.URI;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class AdminAuthController {

	AdminAuthService adminAuthService;

	// POST /api/v1/admin/auth/admins
	@AdminUser
	@PostMapping("/admins")
	public ResponseEntity<BaseResponseDTO<Void>> createAdmin(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid RegisterRequestDTO request
	) {
		UUID adminId = principal.getId();
		URI location = adminAuthService.createAdmin(adminId, request);
		return ResponseEntity
				.created(location)
				.body(BaseResponseDTO.success("Admin account created successfully"));
	}

	// POST /api/v1/admin/auth/sessions
	@PostMapping("/sessions")
	public ResponseEntity<BaseResponseDTO<SignInResponseDTO>> signIn(
			@RequestBody @Valid SignInRequestDTO request
	) {
		SignInResponseDTO data = adminAuthService.signIn(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Signed in successfully"));
	}

	// POST /api/v1/admin/auth/passwords/reset-request
	@PostMapping("/passwords/reset-request")
	public ResponseEntity<BaseResponseDTO<ResetTokenResponseDTO>> requestPasswordReset(
			@RequestBody @Valid IdentifierRequestDTO request
	) {
		ResetTokenResponseDTO data = adminAuthService.requestPasswordReset(request);
		return ResponseEntity
				.accepted()
				.body(BaseResponseDTO.success(data, "Password reset instructions sent if the account exists"));
	}

	// POST /api/v1/admin/auth/passwords/reset
	@PostMapping("/passwords/reset")
	public ResponseEntity<BaseResponseDTO<Void>> resetPassword(
			@RequestBody @Valid ResetPasswordRequestDTO request
	) {
		adminAuthService.resetPassword(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success("Password reset successfully"));
	}

	// POST /api/v1/admin/auth/sessions/refresh
	@AdminUser
	@PostMapping("/sessions/refresh")
	public ResponseEntity<BaseResponseDTO<SignInResponseDTO>> refreshToken(
			@RequestBody @Valid RefreshTokenRequestDTO request
	) {
		SignInResponseDTO data = adminAuthService.refreshToken(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Token refreshed successfully"));
	}
}
