package org.raul.fit_ai.fitness.dto.ai;

import java.util.List;

public record AiWeekDTO(
		Integer weekNumber,
		List<AiDayDTO> days
) {
}
