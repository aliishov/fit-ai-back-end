package org.raul.fit_ai.fitness.controller;

import org.raul.fit_ai.auth.annotation.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ProfileResponseDTO;
import org.raul.fit_ai.fitness.service.UserProfileService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workout/profile")
@RequiredArgsConstructor
@Validated
@AppUser
public class UserProfileController {

	private final UserProfileService userProfileService;

	@GetMapping
	public ResponseEntity<BaseResponseDTO<ProfileResponseDTO>> getProfile(
			@AuthenticationPrincipal UserPrincipal principal
	) {
		ProfileResponseDTO data = userProfileService.getProfile(principal.getId());
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Profile fetched successfully"));
	}

	@PutMapping
	public ResponseEntity<BaseResponseDTO<ProfileResponseDTO>> createOrUpdateProfile(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ProfileRequestDTO request
	) {
		ProfileResponseDTO data = userProfileService.createOrUpdateProfile(principal.getId(), request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Profile saved successfully"));
	}
}
