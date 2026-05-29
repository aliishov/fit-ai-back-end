package org.raul.fit_ai.fitness.controller;

import org.raul.fit_ai.auth.annotation.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ProgressResponseDTO;
import org.raul.fit_ai.fitness.service.ProgressService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@AppUser
public class ProgressController {

	ProgressService progressService;

	@PostMapping
	public ResponseEntity<BaseResponseDTO<ProgressResponseDTO>> recordProgress(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid RecordProgressRequestDTO request
	) {
		ProgressResponseDTO data = progressService.recordProgress(principal, request);
		return ResponseEntity
				.created(URI.create("/api/v1/progress/records/" + data.id()))
				.body(BaseResponseDTO.success(data, "Progress recorded successfully"));
	}

	@GetMapping
	public ResponseEntity<BaseResponseDTO<List<ProgressResponseDTO>>> getProgressHistory(
			@AuthenticationPrincipal UserPrincipal principal
	) {
		List<ProgressResponseDTO> data = progressService.getProgressHistory(principal);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Progress history fetched successfully"));
	}

	@GetMapping("/latest")
	public ResponseEntity<BaseResponseDTO<ProgressResponseDTO>> getLatestProgress(
			@AuthenticationPrincipal UserPrincipal principal
	) {
		ProgressResponseDTO data = progressService.getLatestProgress(principal);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Latest progress fetched successfully"));
	}

	@GetMapping("/records/{progressId}")
	public ResponseEntity<BaseResponseDTO<ProgressResponseDTO>> getProgressRecord(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID progressId
	) {
		ProgressResponseDTO data = progressService.getProgressRecord(principal, progressId);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Progress record fetched successfully"));
	}

	@GetMapping({"/plans/{planId}", "/{planId}"})
	public ResponseEntity<BaseResponseDTO<List<ProgressResponseDTO>>> getProgress(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID planId
	) {
		List<ProgressResponseDTO> data = progressService.getProgress(principal, planId);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Progress fetched successfully"));
	}
}
