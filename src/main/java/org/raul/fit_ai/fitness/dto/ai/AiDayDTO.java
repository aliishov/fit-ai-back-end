package org.raul.fit_ai.fitness.dto.ai;

import java.util.List;

public record AiDayDTO(
		Integer dayNumber,
		String focus,
		String notes,
		List<AiExerciseDTO> exercises
) {
}
