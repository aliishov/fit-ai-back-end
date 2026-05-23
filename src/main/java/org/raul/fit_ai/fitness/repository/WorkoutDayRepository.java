package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.WorkoutDay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkoutDayRepository extends JpaRepository<WorkoutDay, UUID> {
}
