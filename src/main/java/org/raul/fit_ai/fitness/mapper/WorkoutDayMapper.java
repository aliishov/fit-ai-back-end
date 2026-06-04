package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.ai.AiDayDTO;
import org.raul.fit_ai.fitness.model.WorkoutDay;
import org.raul.fit_ai.fitness.model.WorkoutWeek;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@UtilityClass
@Slf4j
public class WorkoutDayMapper {

	public static WorkoutDay toEntity(WorkoutWeek week, AiDayDTO aiDay) {
		return WorkoutDay.builder()
				.workoutWeek(week)
				.dayNumber(aiDay.dayNumber())
				.focus(parseMuscleGroup(aiDay.focus()))
				.notes(aiDay.notes())
				.build();
	}

	private static MuscleGroup parseMuscleGroup(String focus) {
		if (focus == null || focus.isBlank()) {
			log.warn("Missing muscle group from AI - defaulting to FULL_BODY");
			return MuscleGroup.FULL_BODY;
		}

		try {
			return MuscleGroup.valueOf(focus.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			log.warn("Unknown muscle group from AI - defaulting to FULL_BODY");
			return MuscleGroup.FULL_BODY;
		}
	}
}
