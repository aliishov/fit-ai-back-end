package org.raul.fit_ai.fitness.controller;

import org.raul.fit_ai.auth.annotation.AdminUser;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseUpdateRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ExerciseResponseDTO;
import org.raul.fit_ai.fitness.service.ExerciseService;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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
		URI data = exerciseService.createExercise(request);
		return ResponseEntity
				.created(data)
				.body(BaseResponseDTO.success("Exercise created successfully"));
	}

	@GetMapping
	public ResponseEntity<BaseResponseDTO<List<ExerciseResponseDTO>>> getExercises() {
		List<ExerciseResponseDTO> data = exerciseService.getExercises();
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Exercises fetched successfully"));
	}

	@GetMapping("/{exerciseId}")
	public ResponseEntity<BaseResponseDTO<ExerciseResponseDTO>> getExercise(
			@PathVariable Long exerciseId
	) {
		ExerciseResponseDTO data = exerciseService.getExercise(exerciseId);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Exercise fetched successfully"));
	}

	@PatchMapping("/{exerciseId}")
	@AdminUser
	public ResponseEntity<BaseResponseDTO<ExerciseResponseDTO>> updateExercise(
			@PathVariable Long exerciseId,
			@RequestBody @Valid ExerciseUpdateRequestDTO request
	) {
		ExerciseResponseDTO data = exerciseService.updateExercise(exerciseId, request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(data, "Exercise updated successfully"));
	}

	@DeleteMapping("/{exerciseId}")
	@AdminUser
	public ResponseEntity<Void> deleteExercise(
			@PathVariable Long exerciseId
	) {
		exerciseService.deleteExercise(exerciseId);
		return ResponseEntity.noContent().build();
	}
}
