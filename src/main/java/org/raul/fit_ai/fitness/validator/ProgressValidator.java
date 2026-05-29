package org.raul.fit_ai.fitness.validator;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.common.exception.BadRequestException;
import org.raul.fit_ai.common.exception.UnauthorizedException;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProgressValidator {

	private static final BigDecimal MIN_WEIGHT_KG = new BigDecimal("20.0");
	private static final BigDecimal MAX_WEIGHT_KG = new BigDecimal("300.0");
	private static final int MAX_WEIGHT_INTEGER_DIGITS = 3;
	private static final int MAX_WEIGHT_FRACTION_DIGITS = 2;
	private static final int MIN_HEIGHT_CM = 50;
	private static final int MAX_HEIGHT_CM = 250;
	private static final int MAX_NOTES_LENGTH = 1000;

	private final WorkoutPlanRepository workoutPlanRepository;

	public NormalizedProgressRecord validateAndNormalizeRecordRequest(
			UserPrincipal principal,
			RecordProgressRequestDTO request
	) {
		UUID userId = requireUserId(principal);
		if (request == null) {
			throw new BadRequestException("Progress request is required");
		}

		validateWeight(request.weightKg());
		validateHeight(request.heightCm());
		validatePlanId(request.planId());
		validatePlanBelongsToUser(request.planId(), userId);
		validatePlanIsActiveForUser(request.planId(), userId);

		String notes = normalizeNotes(request.notes());
		validateNotes(notes);

		return new NormalizedProgressRecord(
				userId,
				request.weightKg(),
				request.heightCm(),
				notes,
				request.planId()
		);
	}

	public UUID requireUserId(UserPrincipal principal) {
		if (principal == null || principal.getId() == null) {
			throw new UnauthorizedException("Authenticated user is required");
		}
		return principal.getId();
	}

	public void validatePlanForProgressRead(UUID planId, UUID userId) {
		validatePlanId(planId);
		validatePlanBelongsToUser(planId, userId);
	}

	public void validateProgressId(UUID progressId) {
		if (progressId == null) {
			throw new BadRequestException("Progress ID is required");
		}
	}

	private void validateWeight(BigDecimal weightKg) {
		if (weightKg == null) {
			throw new BadRequestException("Weight is required");
		}
		if (weightKg.compareTo(MIN_WEIGHT_KG) < 0) {
			throw new BadRequestException("Weight must be at least 20 kg");
		}
		if (weightKg.compareTo(MAX_WEIGHT_KG) > 0) {
			throw new BadRequestException("Weight must not exceed 300 kg");
		}

		BigDecimal normalized = weightKg.stripTrailingZeros();
		int fractionDigits = Math.max(normalized.scale(), 0);
		int integerDigits = normalized.precision() - normalized.scale();
		if (integerDigits > MAX_WEIGHT_INTEGER_DIGITS || fractionDigits > MAX_WEIGHT_FRACTION_DIGITS) {
			throw new BadRequestException("Weight must have at most 3 integer digits and 2 decimal places");
		}
	}

	private void validateHeight(Integer heightCm) {
		if (heightCm == null) {
			throw new BadRequestException("Height is required");
		}
		if (heightCm < MIN_HEIGHT_CM) {
			throw new BadRequestException("Height must be at least 50 cm");
		}
		if (heightCm > MAX_HEIGHT_CM) {
			throw new BadRequestException("Height must not exceed 250 cm");
		}
	}

	private void validateNotes(String notes) {
		if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
			throw new BadRequestException("Notes must not exceed 1000 characters");
		}
	}

	private String normalizeNotes(String notes) {
		if (notes == null) {
			return null;
		}

		String normalized = notes.trim();
		return normalized.isBlank() ? null : normalized;
	}

	private void validatePlanId(UUID planId) {
		if (planId == null) {
			throw new BadRequestException("Plan ID is required");
		}
	}

	private void validatePlanBelongsToUser(UUID planId, UUID userId) {
		if (!workoutPlanRepository.existsByIdAndUserId(planId, userId)) {
			throw new EntityNotFoundException("Workout plan not found");
		}
	}

	private void validatePlanIsActiveForUser(UUID planId, UUID userId) {
		if (!workoutPlanRepository.existsByIdAndUserIdAndStatus(planId, userId, PlanStatus.ACTIVE)) {
			throw new BadRequestException("Workout plan is not active");
		}
	}

	public record NormalizedProgressRecord(
			UUID userId,
			BigDecimal weightKg,
			Integer heightCm,
			String notes,
			UUID planId
	) {
	}
}
