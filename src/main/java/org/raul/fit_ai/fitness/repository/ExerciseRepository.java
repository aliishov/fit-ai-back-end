package org.raul.fit_ai.fitness.repository;

import org.raul.fit_ai.fitness.model.Exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
	boolean existsByName(String name);
}
