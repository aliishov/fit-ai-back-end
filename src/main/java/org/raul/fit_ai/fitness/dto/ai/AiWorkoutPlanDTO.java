package org.raul.fit_ai.fitness.dto.ai;

import java.util.List;

public record AiWorkoutPlanDTO(
		String aiNotes,
		List<AiWeekDTO> weeks
) {
}
