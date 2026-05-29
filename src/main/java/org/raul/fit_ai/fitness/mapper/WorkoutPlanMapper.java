package org.raul.fit_ai.fitness.mapper;

import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.response.PlanResponseDTO;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
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

	public static PlanResponseDTO toResponseDto(WorkoutPlan plan) {
		return new  PlanResponseDTO(
				plan.getId(),
				plan.getUserId(),
				plan.getActivityType(),
				plan.getStatus(),
				plan.getDurationWeeks(),
				plan.getSessionsPerWeek(),
				plan.getStartsAt(),
				plan.getEndsAt()
		);
	}
}
