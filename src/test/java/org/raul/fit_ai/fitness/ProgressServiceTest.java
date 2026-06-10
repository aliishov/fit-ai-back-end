package org.raul.fit_ai.fitness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.raul.fit_ai.fitness.service.ProgressService;
import org.raul.fit_ai.fitness.repository.UserProgressRepository;
import org.raul.fit_ai.fitness.validator.ProgressValidator;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.dto.response.ProgressResponseDTO;
import org.raul.fit_ai.fitness.validator.ProgressValidator.NormalizedProgressRecord;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    UserProgressRepository userProgressRepository;

    @Mock
    ProgressValidator progressValidator;

    ProgressService service;

    @BeforeEach
    void setUp() {
        service = new ProgressService(userProgressRepository, progressValidator);
    }

    @Test
    void recordProgress_savesAndReturnsDto() {
        UUID userId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        AppUser appUser = AppUser.builder().id(userId).email("u@example.com").build();
        UserPrincipal principal = new UserPrincipal(appUser);

        RecordProgressRequestDTO request = new RecordProgressRequestDTO(
                BigDecimal.valueOf(75),
                175,
                "Felt good",
                planId
        );

        NormalizedProgressRecord normalized = new NormalizedProgressRecord(
                userId,
                request.weightKg(),
                request.heightCm(),
                request.notes(),
                request.planId()
        );

        when(progressValidator.validateAndNormalizeRecordRequest(principal, request)).thenReturn(normalized);

        UserProgress saved = UserProgress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .weightKg(request.weightKg())
                .heightCm(request.heightCm())
                .notes(request.notes())
                .planId(planId)
                .recordedAt(OffsetDateTime.now())
                .build();

        when(userProgressRepository.save(any(UserProgress.class))).thenReturn(saved);

        ProgressResponseDTO resp = service.recordProgress(principal, request);

        assertNotNull(resp);
        assertEquals(saved.getUserId(), resp.userId());
        assertEquals(saved.getPlanId(), resp.planId());
        verify(userProgressRepository).save(any(UserProgress.class));
    }

    @Test
    void getLatestProgress_returnsDto_whenPresent() {
        UUID userId = UUID.randomUUID();
        AppUser appUser = AppUser.builder().id(userId).email("u@example.com").build();
        UserPrincipal principal = new UserPrincipal(appUser);

        when(progressValidator.requireUserId(principal)).thenReturn(userId);

        UserProgress latest = UserProgress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .weightKg(BigDecimal.valueOf(80))
                .heightCm(175)
                .notes("note")
                .planId(UUID.randomUUID())
                .recordedAt(OffsetDateTime.now())
                .build();

        when(userProgressRepository.findFirstByUserIdOrderByRecordedAtDesc(userId)).thenReturn(Optional.of(latest));

        var dto = service.getLatestProgress(principal);

        assertNotNull(dto);
        assertEquals(latest.getUserId(), dto.userId());
    }

    @Test
    void getLatestProgress_throwsWhenAbsent() {
        UUID userId = UUID.randomUUID();
        AppUser appUser = AppUser.builder().id(userId).email("u@example.com").build();
        UserPrincipal principal = new UserPrincipal(appUser);

        when(progressValidator.requireUserId(principal)).thenReturn(userId);
        when(userProgressRepository.findFirstByUserIdOrderByRecordedAtDesc(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getLatestProgress(principal));
    }

    @Test
    void getProgressRecord_returnsDto() {
        UUID userId = UUID.randomUUID();
        UUID progressId = UUID.randomUUID();
        AppUser appUser = AppUser.builder().id(userId).email("u@example.com").build();
        UserPrincipal principal = new UserPrincipal(appUser);

        when(progressValidator.requireUserId(principal)).thenReturn(userId);
        // progressValidator.validateProgressId is a void method — mock does nothing by default

        UserProgress progress = UserProgress.builder()
                .id(progressId)
                .userId(userId)
                .weightKg(BigDecimal.valueOf(78))
                .heightCm(175)
                .notes("n")
                .planId(UUID.randomUUID())
                .recordedAt(OffsetDateTime.now())
                .build();

        when(userProgressRepository.findByIdAndUserId(progressId, userId)).thenReturn(Optional.of(progress));

        var dto = service.getProgressRecord(principal, progressId);
        assertEquals(progress.getId(), dto.id());
    }

    @Test
    void getProgress_returnsList() {
        UUID userId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        AppUser appUser = AppUser.builder().id(userId).email("u@example.com").build();
        UserPrincipal principal = new UserPrincipal(appUser);

        when(progressValidator.requireUserId(principal)).thenReturn(userId);
        doNothing().when(progressValidator).validatePlanForProgressRead(planId, userId);

        UserProgress p = UserProgress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .weightKg(BigDecimal.valueOf(78))
                .heightCm(175)
                .notes("n")
                .planId(planId)
                .recordedAt(OffsetDateTime.now())
                .build();

        when(userProgressRepository.findByUserIdAndPlanIdOrderByRecordedAtDesc(userId, planId)).thenReturn(List.of(p));

        var list = service.getProgress(principal, planId);
        assertEquals(1, list.size());
    }
}
