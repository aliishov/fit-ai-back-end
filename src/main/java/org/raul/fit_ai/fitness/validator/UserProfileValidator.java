package org.raul.fit_ai.fitness.validator;

import org.raul.fit_ai.common.exception.BadRequestException;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class UserProfileValidator {

	private static final BigDecimal MIN_WEIGHT_KG = new BigDecimal("20.0");
	private static final BigDecimal MAX_WEIGHT_KG = new BigDecimal("300.0");
	private static final int MAX_WEIGHT_INTEGER_DIGITS = 3;
	private static final int MAX_WEIGHT_FRACTION_DIGITS = 2;
	private static final int MIN_HEIGHT_CM = 50;
	private static final int MAX_HEIGHT_CM = 250;
	private static final int MIN_AGE = 10;
	private static final int MAX_AGE = 120;
	private static final int MIN_SESSIONS_PER_WEEK = 1;
	private static final int MAX_SESSIONS_PER_WEEK = 7;
	private static final int MAX_LIMITATIONS_LENGTH = 500;

	public static NormalizedUserProfile validateAndNormalize(UUID userId, ProfileRequestDTO request) {
		validateUserId(userId);
		if (request == null) {
			throw new BadRequestException("Profile request is required");
		}

		validateRequired(request.activityType(), "Activity type");
		validateWeight(request.weightKg());
		validateHeight(request.heightCm());
		validateAge(request.age());
		validateRequired(request.gender(), "Gender");
		validateRequired(request.goal(), "Goal");
		validateRequired(request.fitnessLevel(), "Fitness level");
		validateSessionsPerWeek(request.sessionsPerWeek());

		String limitations = normalizeLimitations(request.limitations());
		validateLimitations(limitations);

		return new NormalizedUserProfile(
				userId,
				request.activityType(),
				request.weightKg(),
				request.heightCm(),
				request.age(),
				request.gender(),
				request.goal(),
				request.fitnessLevel(),
				request.sessionsPerWeek(),
				limitations
		);
	}

	public static void validateUserId(UUID userId) {
		if (userId == null) {
			throw new BadRequestException("User ID is required");
		}
	}

	public static boolean isComplete(UserProfile profile) {
		if (profile == null) {
			return false;
		}

		try {
			validateRequired(profile.getActivityType(), "Activity type");
			validateWeight(profile.getWeightKg());
			validateHeight(profile.getHeightCm());
			validateAge(profile.getAge());
			validateRequired(profile.getGender(), "Gender");
			validateRequired(profile.getGoal(), "Goal");
			validateRequired(profile.getFitnessLevel(), "Fitness level");
			validateSessionsPerWeek(profile.getSessionsPerWeek());
			validateLimitations(normalizeLimitations(profile.getLimitations()));
			return true;
		} catch (BadRequestException ex) {
			return false;
		}
	}

	private static void validateWeight(BigDecimal weightKg) {
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

	private static void validateHeight(Integer heightCm) {
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

	private static void validateAge(Integer age) {
		if (age == null) {
			throw new BadRequestException("Age is required");
		}
		if (age < MIN_AGE) {
			throw new BadRequestException("Age must be at least 10");
		}
		if (age > MAX_AGE) {
			throw new BadRequestException("Age must not exceed 120");
		}
	}

	private static void validateSessionsPerWeek(Integer sessionsPerWeek) {
		if (sessionsPerWeek == null) {
			throw new BadRequestException("Sessions per week is required");
		}
		if (sessionsPerWeek < MIN_SESSIONS_PER_WEEK || sessionsPerWeek > MAX_SESSIONS_PER_WEEK) {
			throw new BadRequestException("Sessions per week must be between 1 and 7");
		}
	}

	private static String normalizeLimitations(String limitations) {
		if (limitations == null) {
			return null;
		}

		String normalized = limitations.trim();
		return normalized.isBlank() ? null : normalized;
	}

	private static void validateLimitations(String limitations) {
		if (limitations != null && limitations.length() > MAX_LIMITATIONS_LENGTH) {
			throw new BadRequestException("Limitations must not exceed 500 characters");
		}
	}

	private static void validateRequired(Object value, String fieldName) {
		if (value == null) {
			throw new BadRequestException(fieldName + " is required");
		}
	}

	public record NormalizedUserProfile(
			UUID userId,
			ActivityType activityType,
			BigDecimal weightKg,
			Integer heightCm,
			Integer age,
			Gender gender,
			FitnessGoal goal,
			FitnessLevel fitnessLevel,
			Integer sessionsPerWeek,
			String limitations
	) {
	}
}
