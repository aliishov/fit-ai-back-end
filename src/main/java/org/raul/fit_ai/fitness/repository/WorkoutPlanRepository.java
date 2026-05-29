package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {
	boolean existsByUserIdAndStatus(UUID userId, PlanStatus status);

	boolean existsByUserIdAndStatusIn(UUID userId, Collection<PlanStatus> statuses);

	@Modifying
	@Query("UPDATE WorkoutPlan wp SET wp.status = :status WHERE wp.id = :planId")
	void updateStatus(@Param("planId") UUID planId,
	                  @Param("status") PlanStatus status);

	boolean existsByIdAndStatus(UUID id, PlanStatus status);

	boolean existsByIdAndUserId(UUID id, UUID userId);

	boolean existsByIdAndUserIdAndStatus(UUID id, UUID userId, PlanStatus status);

	Optional<WorkoutPlan> findByIdAndUserId(UUID id, UUID userId);
}
