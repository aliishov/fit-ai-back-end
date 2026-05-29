package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseUpdateRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ExerciseResponseDTO;
import org.raul.fit_ai.fitness.mapper.ExerciseMapper;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;
import org.raul.fit_ai.fitness.validator.ExerciseValidator;
import org.raul.fit_ai.fitness.validator.ExerciseValidator.NormalizedExerciseUpdate;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExerciseService {

	private final ExerciseRepository exerciseRepository;
	private final ExerciseValidator exerciseValidator;

	@Transactional
	public URI createExercise(ExerciseRequestDTO request) {
		ExerciseRequestDTO normalizedRequest = exerciseValidator.validateAndNormalizeCreateRequest(request);
		log.info("Creating new exercise with name: {}", normalizedRequest.name());

		Exercise exercise = ExerciseMapper.toEntity(normalizedRequest);
		exercise = saveAndFlushExercise(exercise);

		log.info("Successfully created exercise [{}]", exercise.getId());
		return URI.create("/api/v1/exercises/" + exercise.getId());
	}

	public List<ExerciseResponseDTO> getExercises() {
		log.info("Retrieving all exercises from database");

		List<Exercise> exercises = exerciseRepository.findAll();

		return exercises.stream()
				.map(ExerciseMapper::toResponseDto)
				.toList();
	}

	public ExerciseResponseDTO getExercise(Long exerciseId) {
		log.info("Retrieving exercise with ID [{}]", exerciseId);

		return ExerciseMapper.toResponseDto(findExercise(exerciseId));
	}

	@Transactional
	public ExerciseResponseDTO updateExercise(Long exerciseId, ExerciseUpdateRequestDTO request) {
		log.info("Updating exercise with ID [{}]", exerciseId);

		Exercise exercise = findExercise(exerciseId);
		NormalizedExerciseUpdate normalizedRequest = exerciseValidator.validateAndNormalizeUpdateRequest(
				request,
				exerciseId
		);
		applyUpdates(exercise, normalizedRequest);

		exercise = saveAndFlushExercise(exercise);

		log.info("Successfully updated exercise [{}]", exercise.getId());
		return ExerciseMapper.toResponseDto(exercise);
	}

	@Transactional
	public void deleteExercise(Long exerciseId) {
		log.info("Deleting exercise with ID [{}]", exerciseId);

		exerciseValidator.validateExerciseId(exerciseId);
		if (!exerciseRepository.existsById(exerciseId)) {
			throw exerciseNotFound(exerciseId);
		}

		exerciseRepository.deleteById(exerciseId);
	}

	private void applyUpdates(Exercise exercise, NormalizedExerciseUpdate request) {
		if (request.name() != null) {
			exercise.setName(request.name());
		}
		if (request.description() != null) {
			exercise.setDescription(request.description());
		}
		if (request.activityType() != null) {
			exercise.setActivityType(request.activityType());
		}
		if (request.muscleGroup() != null) {
			exercise.setMuscleGroup(request.muscleGroup());
		}
		if (request.difficulty() != null) {
			exercise.setDifficulty(request.difficulty());
		}
		if (request.equipmentNeededProvided()) {
			exercise.setEquipmentNeeded(request.equipmentNeeded());
		}
	}

	private Exercise findExercise(Long exerciseId) {
		exerciseValidator.validateExerciseId(exerciseId);
		return exerciseRepository.findById(exerciseId)
				.orElseThrow(() -> exerciseNotFound(exerciseId));
	}

	private EntityNotFoundException exerciseNotFound(Long exerciseId) {
		return new EntityNotFoundException("Exercise with ID [" + exerciseId + "] not found");
	}

	private Exercise saveAndFlushExercise(Exercise exercise) {
		try {
			return exerciseRepository.saveAndFlush(exercise);
		} catch (DataIntegrityViolationException ex) {
			if (exerciseValidator.isExerciseNameUniqueViolation(ex)) {
				throw new DuplicateResourceException(ExerciseValidator.EXERCISE_NAME_DUPLICATE_MESSAGE, ex);
			}
			throw ex;
		}
	}
}
