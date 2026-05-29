package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.response.ProgressResponseDTO;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.raul.fit_ai.fitness.validator.ProgressValidator.NormalizedProgressRecord;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserProgressMapper {

	public static UserProgress toEntity(NormalizedProgressRecord request) {
		return UserProgress.builder()
				.userId(request.userId())
				.weightKg(request.weightKg())
				.heightCm(request.heightCm())
				.notes(request.notes())
				.planId(request.planId())
				.build();
	}

	public static ProgressResponseDTO toResponseDto(UserProgress userProgress) {
		return new ProgressResponseDTO(
				userProgress.getId(),
				userProgress.getUserId(),
				userProgress.getWeightKg(),
				userProgress.getHeightCm(),
				userProgress.getNotes(),
				userProgress.getPlanId(),
				userProgress.getRecordedAt()
		);
	}
}
