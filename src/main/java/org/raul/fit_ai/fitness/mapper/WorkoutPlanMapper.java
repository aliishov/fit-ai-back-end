package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkoutPlanMapper {

	public WorkoutPlan toEntity(UUID userId, ProfileRequestDTO request) {
		return WorkoutPlan.builder()
				.userId(userId)
				.activityType(request.activityType())
				.status(PlanStatus.GENERATING)
				.durationWeeks(request.durationWeeks())
				.sessionsPerWeek(request.sessionsPerWeek())
				.startsAt(request.startsAt())
				.endsAt(request.startsAt().plusWeeks(request.durationWeeks()))
				.build();
	}
}
