package org.raul.fit_ai.fitness.controller;

import jakarta.validation.Valid;
import org.raul.fit_ai.auth.annotation.AppUser;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ScheduleWorkoutRequestDTO;
import org.raul.fit_ai.fitness.dto.request.WorkoutGoalRequestDTO;
import org.raul.fit_ai.fitness.dto.response.PlanIdResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanResponseDTO;
import org.raul.fit_ai.fitness.dto.response.ProfileIdResponseDTO;
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

// TODO
@RestController
@RequestMapping("/api/v1/workout")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@AppUser
public class WorkoutController {

	@GetMapping("/plan/init")
	public ResponseEntity<BaseResponseDTO<Void>> initWorkout(
			@AuthenticationPrincipal UserPrincipal principal
	) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/plan/profile")
	public ResponseEntity<BaseResponseDTO<ProfileIdResponseDTO>> initWorkout(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ProfileRequestDTO request
	) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/plan/goal")
	public ResponseEntity<BaseResponseDTO<Void>> workoutGoals(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid WorkoutGoalRequestDTO request
	) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/plan/schedule")
	public ResponseEntity<BaseResponseDTO<Void>> scheduleWorkout(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ScheduleWorkoutRequestDTO request
	) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/plan/generate")
	public ResponseEntity<BaseResponseDTO<PlanIdResponseDTO>> generateWorkout(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ScheduleWorkoutRequestDTO request
	) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/plan/{planId}")
	public ResponseEntity<BaseResponseDTO<PlanResponseDTO>> getPlan(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable @Valid UUID planId
	) {
		return ResponseEntity.ok().build();
	}
}
