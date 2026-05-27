package org.raul.fit_ai.fitness.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.raul.fit_ai.auth.annotation.AdminUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseUpdateRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ExerciseResponseDTO;
import org.raul.fit_ai.fitness.service.ExerciseService;
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

	ExerciseService exerciseService;

	@PostMapping
	@AdminUser
	public ResponseEntity<BaseResponseDTO<Void>> createExercise(
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

	@PatchMapping("/{exerciseId}")
	@AdminUser
	public ResponseEntity<BaseResponseDTO<ExerciseResponseDTO>> updateExercise(
			@PathVariable Long exerciseId,
			@RequestBody @Valid ExerciseUpdateRequestDTO request
	) {
		return ResponseEntity
				.ok()
				.build();
	}

	@DeleteMapping("/{exerciseId}")
	@AdminUser
	public ResponseEntity<BaseResponseDTO<Void>> deleteExercise(
			@PathVariable Long exerciseId
	) {
		return ResponseEntity
				.noContent()
				.build();
	}
}
