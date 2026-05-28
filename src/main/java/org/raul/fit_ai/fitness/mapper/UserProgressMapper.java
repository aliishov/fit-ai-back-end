package org.raul.fit_ai.fitness.mapper;

import lombok.experimental.UtilityClass;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@UtilityClass
public class UserProgressMapper {

	public UserProgress toEntity(RecordProgressRequestDTO request, UUID userId) {
		return UserProgress.builder()
				.userId(userId)
				.weightKg(request.weightKg())
				.heightCm(request.heightCm())
				.notes(request.notes())
				.planId(request.planId())
				.recordedAt(OffsetDateTime.now())
				.build();
	}
}
