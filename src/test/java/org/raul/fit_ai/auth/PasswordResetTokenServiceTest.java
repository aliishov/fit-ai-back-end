package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.dto.request.VerifyOtpRequestDTO;
import org.raul.fit_ai.auth.dto.response.VerifyOtpResponseDTO;
import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.PasswordResetToken;
import org.raul.fit_ai.auth.repository.PasswordResetTokenRepository;
import org.raul.fit_ai.auth.service.OtpService;
import org.raul.fit_ai.auth.service.PasswordResetTokenService;
import org.raul.fit_ai.common.exception.InvalidTokenException;
import org.raul.fit_ai.common.services.NotificationPublisher;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PasswordResetTokenServiceTest {

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordResetTokenRepository repo;

    @Mock
    private NotificationPublisher publisher;

    @Mock
    private BaseUser user;

    private PasswordResetTokenService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PasswordResetTokenService(otpService, repo, publisher);
    }

    @Test
    void generatePasswordResetToken_createsTokenWithOtpHashAndPublishesNotification() {
        UUID userId = UUID.randomUUID();
        when(user.getId()).thenReturn(userId);
        when(otpService.generateRawOtp()).thenReturn("123456");
        when(otpService.hashOtp("123456")).thenReturn("hash-123456");

        String resetToken = service.generatePasswordResetToken(user, "test@example.com");

        assertNotNull(resetToken);
        verify(repo).invalidateAllByUserId(eq(userId), any(OffsetDateTime.class));

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(repo).save(captor.capture());
        PasswordResetToken saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals("hash-123456", saved.getOtpHash());
        assertEquals(resetToken, saved.getResetToken());

        verify(publisher).publishCritical(any());
    }

    @Test
    void verifyOtp_successMarkesTokenAsVerified() {
        String resetToken = "uuid-token";
        String otp = "654321";
        String otpHash = "hashed-654321";

        PasswordResetToken token = PasswordResetToken.builder()
                .userId(UUID.randomUUID())
                .resetToken(resetToken)
                .otpHash(otpHash)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(false)
                .usedAt(null)
                .build();

        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO(resetToken, otp);

        when(repo.findByResetToken(resetToken)).thenReturn(Optional.of(token));
        doNothing().when(otpService).validate(anyBoolean(), anyBoolean(), anyBoolean(), anyString(), anyString());

        VerifyOtpResponseDTO response = service.verifyOtp(request);

        assertTrue(response.verified());
        assertTrue(token.isVerified());
        verify(repo).save(token);
    }

    @Test
    void verifyOtp_returnsVerifiedTrueWhenAlreadyVerified() {
        String resetToken = "uuid-token";
        String otp = "654321";

        PasswordResetToken token = PasswordResetToken.builder()
                .resetToken(resetToken)
                .verified(true)
                .build();

        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO(resetToken, otp);

        when(repo.findByResetToken(resetToken)).thenReturn(Optional.of(token));

        VerifyOtpResponseDTO response = service.verifyOtp(request);

        assertTrue(response.verified());
        verifyNoInteractions(otpService);
    }

    @Test
    void verifyOtp_throwsWhenTokenNotFound() {
        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO("bad-token", "123456");

        when(repo.findByResetToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> service.verifyOtp(request));
    }

    @Test
    void verifyResetToken_successWhenTokenValid() {
        UUID userId = UUID.randomUUID();
        String resetToken = "valid-token";

        PasswordResetToken token = PasswordResetToken.builder()
                .userId(userId)
                .resetToken(resetToken)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(true)
                .usedAt(null)
                .build();

        when(repo.findByResetToken(resetToken)).thenReturn(Optional.of(token));

        assertDoesNotThrow(() -> service.verifyResetToken(resetToken, userId));
    }

    @Test
    void verifyResetToken_throwsWhenTokenNotFound() {
        when(repo.findByResetToken("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.verifyResetToken("missing", UUID.randomUUID()));
    }

    @Test
    void verifyResetToken_throwsWhenUsed() {
        UUID userId = UUID.randomUUID();
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(userId)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(true)
                .usedAt(OffsetDateTime.now())
                .build();

        when(repo.findByResetToken("token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyResetToken("token", userId));
    }

    @Test
    void verifyResetToken_throwsWhenExpired() {
        UUID userId = UUID.randomUUID();
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(userId)
                .expiresAt(OffsetDateTime.now().minusMinutes(10))
                .verified(true)
                .usedAt(null)
                .build();

        when(repo.findByResetToken("token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyResetToken("token", userId));
    }

    @Test
    void verifyResetToken_throwsWhenUserIdMismatch() {
        UUID userId = UUID.randomUUID();
        UUID differentId = UUID.randomUUID();

        PasswordResetToken token = PasswordResetToken.builder()
                .userId(differentId)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(true)
                .usedAt(null)
                .build();

        when(repo.findByResetToken("token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyResetToken("token", userId));
    }

    @Test
    void verifyResetToken_throwsWhenNotVerified() {
        UUID userId = UUID.randomUUID();
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(userId)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(false)
                .usedAt(null)
                .build();

        when(repo.findByResetToken("token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyResetToken("token", userId));
    }
}
