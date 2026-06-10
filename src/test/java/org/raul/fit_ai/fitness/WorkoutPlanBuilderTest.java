package org.raul.fit_ai.fitness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.raul.fit_ai.fitness.service.ai.WorkoutPlanBuilder;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.dto.ai.AiWeekDTO;
import org.raul.fit_ai.fitness.dto.ai.AiDayDTO;
import org.raul.fit_ai.fitness.dto.ai.AiExerciseDTO;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.repository.WorkoutWeekRepository;
import org.raul.fit_ai.fitness.repository.WorkoutDayRepository;
import org.raul.fit_ai.fitness.repository.WorkoutDayExerciseRepository;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanBuilderTest {

    @Mock
    WorkoutWeekRepository weekRepository;

    @Mock
    WorkoutDayRepository dayRepository;

    @Mock
    WorkoutDayExerciseRepository dayExerciseRepository;

    @Mock
    ExerciseRepository exerciseRepository;

    WorkoutPlanBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new WorkoutPlanBuilder(weekRepository, dayRepository, dayExerciseRepository, exerciseRepository);
    }

    @Test
    void buildAndSave_marksNeedsReviewWhenNoValidExercises() {
        WorkoutPlan plan = WorkoutPlan.builder()
                .id(UUID.randomUUID())
                .status(PlanStatus.GENERATING)
                .build();

        AiExerciseDTO aiEx = new AiExerciseDTO(99L, 3, 10, 60, 30, 1, "n");
        AiDayDTO aiDay = new AiDayDTO(1, "CHEST", "notes", List.of(aiEx));
        AiWeekDTO aiWeek = new AiWeekDTO(1, List.of(aiDay));
        AiWorkoutPlanDTO aiPlan = new AiWorkoutPlanDTO("notes", List.of(aiWeek));

        when(weekRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(dayRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        builder.buildAndSave(plan, aiPlan, Set.of(1L)); // allowed ids don't include 99L

        assertEquals(PlanStatus.NEEDS_REVIEW, plan.getStatus());
        verify(dayExerciseRepository, never()).save(any());
        verify(weekRepository).save(any());
        verify(dayRepository).save(any());
    }

    @Test
    void buildAndSave_savesDayExercisesForValidIds() {
        WorkoutPlan plan = WorkoutPlan.builder()
                .id(UUID.randomUUID())
                .status(PlanStatus.GENERATING)
                .build();

        AiExerciseDTO aiEx = new AiExerciseDTO(1L, 3, 10, 60, 30, 1, "n");
        AiDayDTO aiDay = new AiDayDTO(1, "CHEST", "notes", List.of(aiEx));
        AiWeekDTO aiWeek = new AiWeekDTO(1, List.of(aiDay));
        AiWorkoutPlanDTO aiPlan = new AiWorkoutPlanDTO("notes", List.of(aiWeek));

        when(weekRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(dayRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise ex = Exercise.builder().id(1L).name("e").build();
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(ex));
        when(dayExerciseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        builder.buildAndSave(plan, aiPlan, Set.of(1L));

        verify(dayExerciseRepository, times(1)).save(any());
        assertNotEquals(PlanStatus.NEEDS_REVIEW, plan.getStatus());
    }
}
