package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.model.UserProfile;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UserProfileMapper {

	public static UserProfile toEntity(ProfileRequestDTO request, UUID userId) {
		return UserProfile.builder()
				.userId(userId)
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
}
