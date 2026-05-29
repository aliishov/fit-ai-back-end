package org.raul.fit_ai.fitness.controller;

import org.raul.fit_ai.auth.annotation.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.GenerateWorkoutRequestDTO;
import org.raul.fit_ai.fitness.dto.response.InitResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanIdResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanResponseDTO;
import org.raul.fit_ai.fitness.service.WorkoutService;

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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workout")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@AppUser
public class WorkoutController {

	WorkoutService workoutService;

	@GetMapping("/plan/init")
	public ResponseEntity<BaseResponseDTO<InitResponseDTO>> initWorkout(
			@AuthenticationPrincipal UserPrincipal principal
	) {
		InitResponseDTO data = workoutService.initWorkout(principal);
		return ResponseEntity.ok(BaseResponseDTO.success(data));
	}

	@PostMapping("/plan/generate")
	public ResponseEntity<BaseResponseDTO<PlanIdResponseDTO>> generateWorkout(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid GenerateWorkoutRequestDTO request
	) {
		PlanIdResponseDTO data = workoutService.generateWorkout(principal, request);
		return ResponseEntity
				.accepted()
				.body(BaseResponseDTO.success(data, "Plan generation started"));
	}

	@GetMapping("/plan/{planId}")
	public ResponseEntity<BaseResponseDTO<PlanResponseDTO>> getPlan(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable @Valid UUID planId
	) {
		PlanResponseDTO data = workoutService.getPlan(principal, planId);
		return ResponseEntity.ok(BaseResponseDTO.success(data));
	}
}
