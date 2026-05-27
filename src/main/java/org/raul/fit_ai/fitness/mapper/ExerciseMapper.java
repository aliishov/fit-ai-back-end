package org.raul.fit_ai.fitness.mapper;

import lombok.experimental.UtilityClass;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ExerciseResponseDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.springframework.stereotype.Component;

@Component
@UtilityClass
public class ExerciseMapper {

	public static Exercise toEntity(ExerciseRequestDTO request) {
		return Exercise
				.builder()
				.name(request.name())
				.description(request.description())
				.activityType(request.activityType())
				.muscleGroup(request.muscleGroup())
				.difficulty(request.difficulty())
				.equipmentNeeded(request.equipmentNeeded())
				.build();
	}

	public static ExerciseResponseDTO toResponseDto(Exercise exercise) {
		return new ExerciseResponseDTO(
				exercise.getId(),
				exercise.getName(),
				exercise.getDescription(),
				exercise.getActivityType(),
				exercise.getMuscleGroup(),
				exercise.getDifficulty(),
				exercise.getEquipmentNeeded()
		);
	}
}
