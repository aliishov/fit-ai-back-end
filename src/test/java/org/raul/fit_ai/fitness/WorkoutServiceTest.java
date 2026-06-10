package org.raul.fit_ai.fitness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.raul.fit_ai.fitness.service.WorkoutService;
import org.raul.fit_ai.fitness.service.WorkoutPlanGenerationService;
import org.raul.fit_ai.fitness.service.UserProfileService;
import org.raul.fit_ai.fitness.repository.WorkoutPlanRepository;
import org.raul.fit_ai.fitness.dto.request.GenerateWorkoutRequestDTO;
import org.raul.fit_ai.fitness.dto.response.InitResponseDTO;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.WorkoutPlan;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    UserProfileService userProfileService;

    @Mock
    WorkoutPlanRepository workoutPlanRepository;

    @Mock
    WorkoutPlanGenerationService workoutPlanGenerationService;

    WorkoutService service;

    @BeforeEach
    void setUp() {
        service = new WorkoutService(userProfileService, workoutPlanRepository, workoutPlanGenerationService);
    }

    @Test
    void initWorkout_canGeneratePlanTrue() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).email("user@example.com").build();
        UserPrincipal principal = new UserPrincipal(user);

        when(userProfileService.existsByUserId(userId)).thenReturn(true);
        when(userProfileService.hasCompleteProfile(userId)).thenReturn(true);
        when(workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.ACTIVE)).thenReturn(false);
        when(workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.GENERATING)).thenReturn(false);
        when(workoutPlanRepository.existsByUserIdAndStatus(userId, PlanStatus.NEEDS_REVIEW)).thenReturn(false);

        InitResponseDTO dto = service.initWorkout(principal);

        assertTrue(dto.canGeneratePlan());
        assertTrue(dto.hasProfile());
        assertTrue(dto.profileComplete());
    }

    @Test
    void generateWorkout_throwsWhenOpenPlanExists() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).email("user@example.com").build();
        UserPrincipal principal = new UserPrincipal(user);

        GenerateWorkoutRequestDTO request = new GenerateWorkoutRequestDTO(4, LocalDate.now());

        when(workoutPlanRepository.existsByUserIdAndStatusIn(eq(userId), any())).thenReturn(true);

        assertThrows(org.raul.fit_ai.common.exception.BadRequestException.class,
                () -> service.generateWorkout(principal, request));
    }

    @Test
    void generateWorkout_startsGenerationAndReturnsPlanId() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).email("user@example.com").build();
        UserPrincipal principal = new UserPrincipal(user);

        GenerateWorkoutRequestDTO request = new GenerateWorkoutRequestDTO(4, LocalDate.now());

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

        when(workoutPlanRepository.existsByUserIdAndStatusIn(eq(userId), any())).thenReturn(false);
        when(userProfileService.findCompleteByUserId(userId)).thenReturn(profile);

        WorkoutPlan saved = WorkoutPlan.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .status(PlanStatus.GENERATING)
                .durationWeeks(request.durationWeeks())
                .activityType(profile.getActivityType())
                .sessionsPerWeek(profile.getSessionsPerWeek())
                .startsAt(request.startsAt())
                .endsAt(request.startsAt().plusWeeks(request.durationWeeks()))
                .build();

        when(workoutPlanRepository.save(any())).thenReturn(saved);

        var resp = service.generateWorkout(principal, request);

        assertNotNull(resp);
        assertEquals(saved.getId(), resp.planId());
        verify(workoutPlanGenerationService).generateAsync(saved.getId(), userId);
    }
}
