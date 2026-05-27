package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.WorkoutPlan;

import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {
	boolean existsByUserIdAndStatus(UUID userId, PlanStatus status);
}
