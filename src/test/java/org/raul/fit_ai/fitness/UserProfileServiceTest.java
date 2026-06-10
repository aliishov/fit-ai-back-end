package org.raul.fit_ai.fitness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.raul.fit_ai.fitness.service.UserProfileService;
import org.raul.fit_ai.fitness.repository.UserProfileRepository;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.common.exception.BadRequestException;

import jakarta.persistence.EntityNotFoundException;
import org.raul.fit_ai.fitness.dto.response.ProfileResponseDTO;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    UserProfileRepository userProfileRepository;

    UserProfileService service;

    @BeforeEach
    void setUp() {
        service = new UserProfileService(userProfileRepository);
    }

    @Test
    void existsByUserId_nullThrows() {
        assertThrows(BadRequestException.class, () -> service.existsByUserId(null));
    }

    @Test
    void createOrUpdateProfile_createsNewProfile() {
        UUID userId = UUID.randomUUID();
        ProfileRequestDTO request = new ProfileRequestDTO(
                ActivityType.FITNESS,
                BigDecimal.valueOf(70),
                175,
                30,
                Gender.MALE,
                FitnessGoal.GENERAL,
                FitnessLevel.BEGINNER,
                3,
                null
        );

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserProfile saved = UserProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .activityType(ActivityType.FITNESS)
                .weightKg(BigDecimal.valueOf(70))
                .heightCm(175)
                .age(30)
                .gender(Gender.MALE)
                .goal(FitnessGoal.GENERAL)
                .fitnessLevel(FitnessLevel.BEGINNER)
                .sessionsPerWeek(3)
                .build();

        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(saved);

        ProfileResponseDTO dto = service.createOrUpdateProfile(userId, request);

        assertNotNull(dto);
        assertEquals(saved.getUserId(), dto.userId());
        assertEquals(saved.getActivityType(), dto.activityType());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void findByUserId_notFound() {
        UUID userId = UUID.randomUUID();
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByUserId(userId));
    }

    @Test
    void findCompleteByUserId_throwsWhenIncomplete() {
        UUID userId = UUID.randomUUID();

        // Build an incomplete profile (missing weight)
        UserProfile incomplete = UserProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .activityType(ActivityType.FITNESS)
                .heightCm(175)
                .age(30)
                .gender(Gender.MALE)
                .goal(FitnessGoal.GENERAL)
                .fitnessLevel(FitnessLevel.BEGINNER)
                .sessionsPerWeek(3)
                .build();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(incomplete));

        assertThrows(BadRequestException.class, () -> service.findCompleteByUserId(userId));
    }
}
