package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseUpdateRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ExerciseResponseDTO;
import org.raul.fit_ai.fitness.mapper.ExerciseMapper;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExerciseService {

	ExerciseRepository exerciseRepository;

	@Transactional
	public URI createExercise(ExerciseRequestDTO request) {
		log.info("Creating new exercise with name: {}", request.name());

		if (exerciseRepository.existsByName(request.name())) {
			throw new DuplicateResourceException("An exercise with this name already exists");
		}

		Exercise exercise = ExerciseMapper.toEntity(request);

		log.info("Successfully created exercise [{}]", exercise.getId());
		return URI.create("/api/v1/exercise/" + exercise.getId());
	}

	@Transactional(readOnly = true)
	public List<ExerciseResponseDTO> getExercises() {
		log.info("Retrieving all exercises from database");

		List<Exercise> exercises = exerciseRepository.findAll();

		return exercises.stream()
				.map(ExerciseMapper::toResponseDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public ExerciseResponseDTO getExercise(Long exerciseId) {
		log.info("Retrieving exercise with ID [{}]", exerciseId);

		Exercise exercise = exerciseRepository.findById(exerciseId)
				.orElseThrow(() -> new EntityNotFoundException("Exercise with ID [" + exerciseId + "] not found"));

		return ExerciseMapper.toResponseDto(exercise);
	}

	@Transactional
	public ExerciseResponseDTO updateExercise(Long exerciseId, ExerciseUpdateRequestDTO request) {
		log.info("Updating exercise with name: {}", request.name());

		Exercise exercise = exerciseRepository.findById(exerciseId)
				.orElseThrow(() -> new EntityNotFoundException("Exercise with ID [" + exerciseId + "] not found"));

		if (!request.name().isEmpty()) exercise.setName(request.name());
		if (!request.description().isEmpty()) exercise.setDescription(request.description());
		if (request.activityType() != null) exercise.setActivityType(request.activityType());
		if (request.muscleGroup()!= null) exercise.setMuscleGroup(request.muscleGroup());
		if (request.difficulty() != null) exercise.setDifficulty(request.difficulty());
		if (!request.equipmentNeeded().isEmpty()) exercise.setEquipmentNeeded(request.equipmentNeeded());

		exercise = exerciseRepository.save(exercise);

		log.info("Successfully updated exercise [{}]", exercise.getId());
		return ExerciseMapper.toResponseDto(exercise);
	}

	@Transactional
	public void deleteExercise(Long exerciseId) {
		log.info("Deleting exercise with ID [{}]", exerciseId);
		exerciseRepository.deleteById(exerciseId);
	}

	public Exercise getReferenceById(Long id) {
		return exerciseRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Exercise with ID [" + id + "] not found"));
	}

	public List<Exercise> findByActivityTypeAndDifficulty(ActivityType activityType, FitnessLevel fitnessLevel) {
		return exerciseRepository.findByActivityTypeAndDifficulty(activityType, fitnessLevel);
	}
}
