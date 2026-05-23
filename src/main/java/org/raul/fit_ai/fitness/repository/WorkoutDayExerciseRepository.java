package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.WorkoutDayExercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkoutDayExerciseRepository extends JpaRepository<WorkoutDayExercise, UUID> {
}
