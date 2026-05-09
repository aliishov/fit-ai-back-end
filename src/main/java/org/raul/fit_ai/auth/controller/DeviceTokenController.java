package org.raul.fit_ai.auth.controller;

import org.raul.fit_ai.auth.annotation.AppUser;
import org.raul.fit_ai.auth.dto.request.DeviceTokenRequestDTO;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.service.DeviceTokenService;
import org.raul.fit_ai.common.dto.BaseResponseDTO;

import jakarta.validation.Valid;

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

@RestController
@RequestMapping("/api/v1/app/devices/token")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@AppUser
public class DeviceTokenController {

	DeviceTokenService deviceTokenService;

	@PostMapping
	public ResponseEntity<BaseResponseDTO<Void>> saveToken(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid DeviceTokenRequestDTO request
	) {
		deviceTokenService.saveToken(principal, request);
		return ResponseEntity.ok(BaseResponseDTO.success("Device token saved successfully"));
	}
}
