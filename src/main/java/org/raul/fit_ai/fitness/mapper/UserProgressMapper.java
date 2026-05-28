package org.raul.fit_ai.fitness.mapper;

import lombok.experimental.UtilityClass;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ProgressResponseDTO;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@UtilityClass
public class UserProgressMapper {

	public static UserProgress toEntity(RecordProgressRequestDTO request, UUID userId) {
		return UserProgress.builder()
				.userId(userId)
				.weightKg(request.weightKg())
				.heightCm(request.heightCm())
				.notes(request.notes())
				.planId(request.planId())
				.recordedAt(OffsetDateTime.now())
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
