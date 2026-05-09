package org.raul.fit_ai.auth.controller;

import org.raul.fit_ai.auth.service.PasswordResetTokenService;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.auth.dto.request.VerifyOtpRequestDTO;
import org.raul.fit_ai.auth.dto.response.VerifyOtpResponseDTO;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class OtpController {

	PasswordResetTokenService passwordResetTokenService;

	@PostMapping("/verify")
	public ResponseEntity<BaseResponseDTO<VerifyOtpResponseDTO>> verifyOtp(
			@RequestBody @Valid VerifyOtpRequestDTO request
	) {
		VerifyOtpResponseDTO data = passwordResetTokenService.verifyOtp(request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "OTP verified successfully"));
	}
}
