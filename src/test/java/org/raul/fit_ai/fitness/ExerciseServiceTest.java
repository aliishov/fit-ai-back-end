package org.raul.fit_ai.fitness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityNotFoundException;

import org.raul.fit_ai.fitness.service.ExerciseService;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;
import org.raul.fit_ai.fitness.validator.ExerciseValidator;
import org.raul.fit_ai.fitness.dto.request.ExerciseRequestDTO;
import org.raul.fit_ai.fitness.dto.request.ExerciseUpdateRequestDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    ExerciseRepository exerciseRepository;

    @Mock
    ExerciseValidator exerciseValidator;

    ExerciseService service;

    @BeforeEach
    void setUp() {
        service = new ExerciseService(exerciseRepository, exerciseValidator);
    }

    @Test
    void createExercise_success() {
        ExerciseRequestDTO req = new ExerciseRequestDTO(
                "Push up",
                "A long description that exceeds fifty characters for testing purposes........",
                ActivityType.FITNESS,
                org.raul.fit_ai.fitness.model.enumerated.MuscleGroup.CHEST,
                FitnessLevel.BEGINNER,
                null
        );

        when(exerciseValidator.validateAndNormalizeCreateRequest(req)).thenReturn(req);

        Exercise saved = Exercise.builder()
                .id(1L)
                .name(req.name())
                .description(req.description())
                .activityType(req.activityType())
                .muscleGroup(req.muscleGroup())
                .difficulty(req.difficulty())
                .build();

        when(exerciseRepository.saveAndFlush(any(Exercise.class))).thenReturn(saved);

        URI uri = service.createExercise(req);
        assertTrue(uri.toString().endsWith("/api/v1/exercises/1"));
    }

    @Test
    void createExercise_duplicateNameThrows() {
        ExerciseRequestDTO req = new ExerciseRequestDTO(
                "Push up",
                "A long description that exceeds fifty characters for testing purposes........",
                ActivityType.FITNESS,
                org.raul.fit_ai.fitness.model.enumerated.MuscleGroup.CHEST,
                FitnessLevel.BEGINNER,
                null
        );

        when(exerciseValidator.validateAndNormalizeCreateRequest(req)).thenReturn(req);
        when(exerciseRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uq_exercises_name_ci"));
        when(exerciseValidator.isExerciseNameUniqueViolation(any())).thenReturn(true);

        assertThrows(org.raul.fit_ai.common.exception.DuplicateResourceException.class, () -> service.createExercise(req));
    }

    @Test
    void getExercises_returnsList() {
        Exercise e = Exercise.builder().id(1L).name("e").description("desc with enough length to be valid........................").activityType(ActivityType.FITNESS).muscleGroup(org.raul.fit_ai.fitness.model.enumerated.MuscleGroup.CHEST).difficulty(FitnessLevel.BEGINNER).build();
        when(exerciseRepository.findAll()).thenReturn(List.of(e));

        var list = service.getExercises();
        assertEquals(1, list.size());
        assertEquals(e.getName(), list.get(0).name());
    }

    @Test
    void getExercise_notFound() {
        when(exerciseRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getExercise(42L));
    }

    @Test
    void updateExercise_appliesUpdates() {
        Long id = 1L;
        Exercise existing = Exercise.builder().id(id).name("old").description("long description that is sufficient........................").activityType(ActivityType.FITNESS).muscleGroup(org.raul.fit_ai.fitness.model.enumerated.MuscleGroup.CHEST).difficulty(FitnessLevel.BEGINNER).build();
        when(exerciseRepository.findById(id)).thenReturn(Optional.of(existing));

        ExerciseUpdateRequestDTO updateReq = new ExerciseUpdateRequestDTO("new name", null, null, null, null, null);
        when(exerciseValidator.validateAndNormalizeUpdateRequest(updateReq, id)).thenReturn(new ExerciseValidator.NormalizedExerciseUpdate(
                "new name",
                null,
                null,
                null,
                null,
                false,
                null
        ));

        when(exerciseRepository.saveAndFlush(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resp = service.updateExercise(id, updateReq);
        assertEquals("new name", resp.name());
    }

    @Test
    void deleteExercise_notFound() {
        Long id = 123L;
        when(exerciseRepository.existsById(id)).thenReturn(false);

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.deleteExercise(id));
    }
}
