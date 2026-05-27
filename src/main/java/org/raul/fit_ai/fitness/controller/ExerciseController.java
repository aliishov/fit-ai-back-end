package org.raul.fit_ai.fitness.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.raul.fit_ai.auth.annotation.AdminUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ExerciseController {

	@PostMapping
	@AdminUser
	public ResponseEntity<BaseResponseDTO<Void>> createExercise(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ExerciseRequestDTO request
	) {
		return ResponseEntity
				.created()
				.build();
	}

	@GetMapping
	public ResponseEntity<BaseResponseDTO<List<ExerciseResponseDTO>>> getExercises() {
		return ResponseEntity
				.ok()
				.build();
	}

	@GetMapping("/{exerciseId}")
	public ResponseEntity<BaseResponseDTO<ExerciseResponseDTO>> getExercise(
			@PathVariable Long exerciseId
	) {
		return ResponseEntity
				.ok()
				.build();
	}

	@PatchMapping
	@AdminUser
	public ResponseEntity<BaseResponseDTO<ExerciseResponseDTO>> updateExercise(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ExerciseUpdateRequestDTO request
	) {
		return ResponseEntity
				.ok()
				.build();
	}

	@DeleteMapping("/{exerciseId}")
	@AdminUser
	public ResponseEntity<BaseResponseDTO<Void>> deleteExercise(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable Long exerciseId
	) {
		return ResponseEntity
				.noContent()
				.build();
	}
}
