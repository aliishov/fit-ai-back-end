package org.raul.fit_ai.fitness.validator;

import org.raul.fit_ai.common.exception.BadRequestException;
import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseUpdateRequestDTO;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ExerciseValidator {

	private static final int MIN_NAME_LENGTH = 2;
	private static final int MAX_NAME_LENGTH = 50;
	private static final int MIN_DESCRIPTION_LENGTH = 50;
	private static final int MAX_EQUIPMENT_LENGTH = 255;
	public static final String EXERCISE_NAME_DUPLICATE_MESSAGE = "An exercise with this name already exists";
	private static final String EXERCISE_NAME_UNIQUE_CONSTRAINT = "uq_exercises_name_ci";

	private final ExerciseRepository exerciseRepository;

	public ExerciseRequestDTO validateAndNormalizeCreateRequest(ExerciseRequestDTO request) {
		if (request == null) {
			throw new BadRequestException("Exercise request is required");
		}

		String name = normalizeName(request.name());
		validateName(name);
		ensureNameIsAvailable(name);

		String description = normalizeDescription(request.description());
		validateDescription(description);

		validateRequired(request.activityType(), "Activity type");
		validateRequired(request.muscleGroup(), "Muscle group");
		validateRequired(request.difficulty(), "Difficulty");

		String equipmentNeeded = normalizeOptionalText(request.equipmentNeeded());
		validateEquipmentNeeded(equipmentNeeded);

		return new ExerciseRequestDTO(
				name,
				description,
				request.activityType(),
				request.muscleGroup(),
				request.difficulty(),
				equipmentNeeded
		);
	}

	public NormalizedExerciseUpdate validateAndNormalizeUpdateRequest(
			ExerciseUpdateRequestDTO request,
			Long exerciseId
	) {
		validateExerciseId(exerciseId);
		if (request == null) {
			throw new BadRequestException("Exercise update request is required");
		}
		if (!hasAnyUpdate(request)) {
			throw new BadRequestException("At least one field must be provided for update");
		}

		String name = null;
		if (request.name() != null) {
			name = normalizeName(request.name());
			validateName(name);
			ensureNameIsAvailableForUpdate(name, exerciseId);
		}

		String description = null;
		if (request.description() != null) {
			description = normalizeDescription(request.description());
			validateDescription(description);
		}

		String equipmentNeeded = null;
		boolean equipmentNeededProvided = request.equipmentNeeded() != null;
		if (equipmentNeededProvided) {
			equipmentNeeded = normalizeOptionalText(request.equipmentNeeded());
			validateEquipmentNeeded(equipmentNeeded);
		}

		return new NormalizedExerciseUpdate(
				name,
				description,
				request.activityType(),
				request.muscleGroup(),
				request.difficulty(),
				equipmentNeededProvided,
				equipmentNeeded
		);
	}

	public void validateExerciseId(Long exerciseId) {
		if (exerciseId == null) {
			throw new BadRequestException("Exercise ID is required");
		}
		if (exerciseId <= 0) {
			throw new BadRequestException("Exercise ID must be positive");
		}
	}

	public boolean isExerciseNameUniqueViolation(DataIntegrityViolationException ex) {
		return containsConstraintName(ex.getMessage())
				|| containsConstraintName(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
	}

	private boolean hasAnyUpdate(ExerciseUpdateRequestDTO request) {
		return request.name() != null
				|| request.description() != null
				|| request.activityType() != null
				|| request.muscleGroup() != null
				|| request.difficulty() != null
				|| request.equipmentNeeded() != null;
	}

	private void ensureNameIsAvailable(String name) {
		if (exerciseRepository.existsByNameIgnoreCase(name)) {
			throw new DuplicateResourceException(EXERCISE_NAME_DUPLICATE_MESSAGE);
		}
	}

	private void ensureNameIsAvailableForUpdate(String name, Long exerciseId) {
		if (exerciseRepository.existsByNameIgnoreCaseAndIdNot(name, exerciseId)) {
			throw new DuplicateResourceException(EXERCISE_NAME_DUPLICATE_MESSAGE);
		}
	}

	private boolean containsConstraintName(String message) {
		return message != null && message.toLowerCase(Locale.ROOT).contains(EXERCISE_NAME_UNIQUE_CONSTRAINT);
	}

	private String normalizeName(String value) {
		return collapseWhitespace(normalizeRequiredText(value, "Name"));
	}

	private String normalizeDescription(String value) {
		return normalizeRequiredText(value, "Description");
	}

	private String normalizeRequiredText(String value, String fieldName) {
		if (value == null) {
			throw new BadRequestException(fieldName + " is required");
		}

		String normalized = value.trim();
		if (normalized.isBlank()) {
			throw new BadRequestException(fieldName + " is required");
		}

		return normalized;
	}

	private String normalizeOptionalText(String value) {
		if (value == null) {
			return null;
		}

		String normalized = collapseWhitespace(value.trim());
		return normalized.isBlank() ? null : normalized;
	}

	private String collapseWhitespace(String value) {
		return value.replaceAll("\\s+", " ");
	}

	private void validateName(String name) {
		if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
			throw new BadRequestException("Name should be between 2 and 50 characters");
		}
	}

	private void validateDescription(String description) {
		if (description.length() < MIN_DESCRIPTION_LENGTH) {
			throw new BadRequestException("Description should be at least 50 characters");
		}
	}

	private void validateEquipmentNeeded(String equipmentNeeded) {
		if (equipmentNeeded != null && equipmentNeeded.length() > MAX_EQUIPMENT_LENGTH) {
			throw new BadRequestException("Equipment needed should not exceed 255 characters");
		}
	}

	private void validateRequired(Object value, String fieldName) {
		if (value == null) {
			throw new BadRequestException(fieldName + " is required");
		}
	}

	public record NormalizedExerciseUpdate(
			String name,
			String description,
			ActivityType activityType,
			MuscleGroup muscleGroup,
			FitnessLevel difficulty,
			boolean equipmentNeededProvided,
			String equipmentNeeded
	) {
	}
}
