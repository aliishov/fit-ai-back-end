package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.UserProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {
	List<UserProgress> findByUserIdAndPlanIdOrderByRecordedAtDesc(UUID userId, UUID planId);

	List<UserProgress> findByUserIdOrderByRecordedAtDesc(UUID userId);

	Optional<UserProgress> findFirstByUserIdOrderByRecordedAtDesc(UUID userId);

	Optional<UserProgress> findByIdAndUserId(UUID id, UUID userId);
}
