package org.raul.fit_ai.fitness.controller;

import jakarta.validation.Valid;
import org.raul.fit_ai.auth.annotation.AppUser;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.dto.BaseResponseDTO;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.response.InitResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanIdResponseDTO;
import org.raul.fit_ai.fitness.dto.response.PlanResponseDTO;
import org.raul.fit_ai.fitness.dto.response.ProfileIdResponseDTO;
import org.raul.fit_ai.fitness.service.WorkoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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
		boolean isReady = workoutService.initWorkout(principal);

		if (!isReady) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(BaseResponseDTO.error("PROFILE_REQUIRED",
							Map.of("profile", List.of("Complete your profile to continue"))));
		}

		return ResponseEntity.ok(BaseResponseDTO.success(new InitResponseDTO(true)));
	}

	@PostMapping("/plan/profile")
	public ResponseEntity<BaseResponseDTO<ProfileIdResponseDTO>> createProfile(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestBody @Valid ProfileRequestDTO request
	) {
		ProfileIdResponseDTO response = workoutService.createProfile(principal, request);
		return ResponseEntity
				.ok(BaseResponseDTO.success(response, "Profile has been filled"));
	}

	@PostMapping("/plan/generate")
	public ResponseEntity<BaseResponseDTO<PlanIdResponseDTO>> generateWorkout(
			@AuthenticationPrincipal UserPrincipal principal
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
