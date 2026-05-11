package org.raul.fit_ai.auth.controller;

import org.raul.fit_ai.auth.dto.request.ChangePasswordRequestDTO;
import org.raul.fit_ai.auth.dto.request.UpdateProfileRequestDTO;
import org.raul.fit_ai.auth.dto.response.AccountResponseDTO;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.service.AccountService;
import org.raul.fit_ai.common.dto.BaseResponseDTO;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/acc/me")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class AccountController {

	AccountService accountService;

	@GetMapping
	public ResponseEntity<BaseResponseDTO<AccountResponseDTO>> getProfile(
			@AuthenticationPrincipal UserPrincipal principal
	) {
		AccountResponseDTO data = accountService.getProfile(principal);
		return ResponseEntity.ok(BaseResponseDTO.success(data));
	}

	@PatchMapping
	public ResponseEntity<BaseResponseDTO<Void>> updateProfile(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid UpdateProfileRequestDTO request
	) {
		accountService.updateProfile(principal, request);
		return ResponseEntity.ok(BaseResponseDTO.success("Profile updated successfully"));
	}

	@PatchMapping
	public ResponseEntity<BaseResponseDTO<Void>> updatePassword(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ChangePasswordRequestDTO request
	) {
		accountService.updatePassword(principal, request);
		return ResponseEntity.ok(BaseResponseDTO.success("Password updated successfully"));
	}
}
