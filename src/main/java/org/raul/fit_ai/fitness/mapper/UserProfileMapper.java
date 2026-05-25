package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserProfileMapper {

	public UserProfile toEntity(ProfileRequestDTO request, UUID userId) {
		return UserProfile.builder()
				.userId(userId)
				.activityType(request.activityType())
				.weightKg(request.weightKg())
				.heightCm(request.heightCm())
				.age(request.age())
				.gender(request.gender())
				.goal(request.goal())
				.fitnessLevel(request.fitnessLevel())
				.sessionsPerWeek()
				.limitations()
				.build();
	}
}
