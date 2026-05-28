package org.raul.fit_ai.fitness.dto.ai;

public record AiExerciseDTO(
		Long exerciseId,
		Integer sets,
		Integer reps,
		Integer durationSeconds,
		Integer restSeconds,
		Integer orderIndex,
		String notes
) {}
