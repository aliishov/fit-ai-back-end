package org.raul.fit_ai.fitness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.raul.fit_ai.fitness.service.WorkoutPlanGenerationService;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;
import org.raul.fit_ai.fitness.repository.UserProfileRepository;
import org.raul.fit_ai.fitness.repository.ExerciseRepository;
import org.raul.fit_ai.fitness.repository.UserProgressRepository;
import org.raul.fit_ai.fitness.service.ai.WorkoutAiService;
import org.raul.fit_ai.fitness.service.ai.WorkoutPlanBuilder;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.common.services.NotificationPublisher;
import org.raul.fit_ai.fitness.client.AppUserClient;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanGenerationServiceTest {

    @Mock
    WorkoutPlanRepository workoutPlanRepository;

    @Mock
    UserProfileRepository userProfileRepository;

    @Mock
    ExerciseRepository exerciseRepository;

    @Mock
    UserProgressRepository userProgressRepository;

    @Mock
    AppUserClient appUserClient;

    @Mock
    WorkoutPlanBuilder workoutPlanBuilder;

    @Mock
    NotificationPublisher notificationPublisher;

    @Mock
    WorkoutAiService workoutAiService;

    WorkoutPlanGenerationService service;

    @BeforeEach
    void setUp() {
        service = new WorkoutPlanGenerationService(
                workoutPlanRepository,
                userProfileRepository,
                exerciseRepository,
                userProgressRepository,
                appUserClient,
                workoutPlanBuilder,
                notificationPublisher,
                workoutAiService
        );
    }

    @Test
    void generateAsync_noExercisesCancelsPlan() {
        UUID planId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(planId)
                .userId(userId)
                .status(PlanStatus.GENERATING)
                .durationWeeks(4)
                .build();

        when(workoutPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.of(plan));

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .activityType(ActivityType.FITNESS)
                .fitnessLevel(FitnessLevel.BEGINNER)
                .sessionsPerWeek(3)
                .weightKg(BigDecimal.valueOf(70))
                .heightCm(175)
                .age(30)
                .gender(Gender.MALE)
                .goal(FitnessGoal.GENERAL)
                .build();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(exerciseRepository.findByActivityTypeAndDifficulty(profile.getActivityType(), profile.getFitnessLevel()))
                .thenReturn(Collections.emptyList());

        service.generateAsync(planId, userId);

        verify(workoutPlanRepository).updateStatus(planId, PlanStatus.CANCELLED);
        verify(workoutAiService, never()).generatePlan(any(), anyList(), anyList(), anyInt());
        verify(notificationPublisher, never()).publish(any());
    }

    @Test
    void generateAsync_withExercisesBuildsPlanAndPublishes() {
        UUID planId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(planId)
                .userId(userId)
                .status(PlanStatus.GENERATING)
                .durationWeeks(6)
                .build();

        when(workoutPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.of(plan));

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .activityType(ActivityType.FITNESS)
                .fitnessLevel(FitnessLevel.BEGINNER)
                .sessionsPerWeek(3)
                .weightKg(BigDecimal.valueOf(70))
                .heightCm(175)
                .age(30)
                .gender(Gender.MALE)
                .goal(FitnessGoal.GENERAL)
                .build();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        Exercise ex = Exercise.builder()
                .id(1L)
                .name("push")
                .description("desc")
                .activityType(ActivityType.FITNESS)
                .difficulty(FitnessLevel.BEGINNER)
                .muscleGroup(MuscleGroup.CHEST)
                .build();

        when(exerciseRepository.findByActivityTypeAndDifficulty(profile.getActivityType(), profile.getFitnessLevel()))
                .thenReturn(List.of(ex));

        when(userProgressRepository.findByUserIdOrderByRecordedAtDesc(userId)).thenReturn(Collections.emptyList());

        AiWorkoutPlanDTO aiPlan = new AiWorkoutPlanDTO("notes", Collections.emptyList());
        when(workoutAiService.generatePlan(eq(profile), anyList(), anyList(), eq(plan.getDurationWeeks())))
                .thenReturn(aiPlan);

        // Run
        service.generateAsync(planId, userId);

        verify(workoutPlanBuilder).buildAndSave(eq(plan), eq(aiPlan), any(Set.class));
        verify(workoutPlanRepository).updateStatus(planId, PlanStatus.ACTIVE);
        verify(notificationPublisher).publish(any());
    }

    @Test
    void generateAsync_handlesExceptionAndCancels() {
        UUID planId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(planId)
                .userId(userId)
                .status(PlanStatus.GENERATING)
                .durationWeeks(4)
                .build();

        when(workoutPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.of(plan));

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .activityType(ActivityType.FITNESS)
                .fitnessLevel(FitnessLevel.BEGINNER)
                .sessionsPerWeek(3)
                .weightKg(BigDecimal.valueOf(70))
                .heightCm(175)
                .age(30)
                .gender(Gender.MALE)
                .goal(FitnessGoal.GENERAL)
                .build();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        Exercise ex = Exercise.builder()
                .id(1L)
                .name("push")
                .description("desc")
                .activityType(ActivityType.FITNESS)
                .difficulty(FitnessLevel.BEGINNER)
                .muscleGroup(MuscleGroup.CHEST)
                .build();

        when(exerciseRepository.findByActivityTypeAndDifficulty(profile.getActivityType(), profile.getFitnessLevel()))
                .thenReturn(List.of(ex));

        when(userProgressRepository.findByUserIdOrderByRecordedAtDesc(userId)).thenReturn(Collections.emptyList());

        when(workoutAiService.generatePlan(eq(profile), anyList(), anyList(), anyInt()))
                .thenThrow(new RuntimeException("AI failure"));

        service.generateAsync(planId, userId);

        verify(workoutPlanRepository).updateStatus(planId, PlanStatus.CANCELLED);
    }
}
