package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.WorkoutWeek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutWeekRepository extends JpaRepository<WorkoutWeek, Long> {
}
