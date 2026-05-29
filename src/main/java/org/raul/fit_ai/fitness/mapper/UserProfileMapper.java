package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.response.ProfileResponseDTO;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.validator.UserProfileValidator.NormalizedUserProfile;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserProfileMapper {

	public static UserProfile toEntity(NormalizedUserProfile request) {
		return UserProfile.builder()
				.userId(request.userId())
				.activityType(request.activityType())
				.weightKg(request.weightKg())
				.heightCm(request.heightCm())
				.age(request.age())
				.gender(request.gender())
				.goal(request.goal())
				.fitnessLevel(request.fitnessLevel())
				.sessionsPerWeek(request.sessionsPerWeek())
				.limitations(request.limitations())
				.build();
	}

	public static void updateEntity(UserProfile profile, NormalizedUserProfile request) {
		profile.setActivityType(request.activityType());
		profile.setWeightKg(request.weightKg());
		profile.setHeightCm(request.heightCm());
		profile.setAge(request.age());
		profile.setGender(request.gender());
		profile.setGoal(request.goal());
		profile.setFitnessLevel(request.fitnessLevel());
		profile.setSessionsPerWeek(request.sessionsPerWeek());
		profile.setLimitations(request.limitations());
	}

	public static ProfileResponseDTO toResponseDto(UserProfile profile) {
		return new ProfileResponseDTO(
				profile.getId(),
				profile.getUserId(),
				profile.getActivityType(),
				profile.getWeightKg(),
				profile.getHeightCm(),
				profile.getAge(),
				profile.getGender(),
				profile.getGoal(),
				profile.getFitnessLevel(),
				profile.getSessionsPerWeek(),
				profile.getLimitations(),
				profile.getCreatedAt(),
				profile.getUpdatedAt()
		);
	}
}
