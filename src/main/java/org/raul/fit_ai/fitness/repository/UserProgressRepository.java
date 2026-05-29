package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.UserProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {
	List<UserProgress> findByUserIdAndPlanId(UUID userId, UUID planId);

	List<UserProgress> findByUserIdOrderByRecordedAtDesc(UUID userId);
}
