package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.OtpToken;
import org.raul.fit_ai.auth.model.enumerated.OtpType;
import org.raul.fit_ai.auth.repository.OtpTokenRepository;
import org.raul.fit_ai.auth.service.OtpService;
import org.raul.fit_ai.common.exception.InvalidOtpException;
import org.raul.fit_ai.common.exception.InvalidTokenException;
import org.raul.fit_ai.common.services.NotificationPublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    @Mock
    private OtpTokenRepository repo;

    @Mock
    private NotificationPublisher publisher;

    @Mock
    private BaseUser user;

    private OtpService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = spy(new OtpService(repo, publisher));
    }

    private String sha256Hex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] h = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : h) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Test
    void generateOtp_savesHashedAndPublishesNotification() throws Exception {
        UUID id = UUID.randomUUID();
        when(user.getId()).thenReturn(id);
        // Force deterministic OTP
        doReturn("123456").when(service).generateRawOtp();

        service.generateOtp(user, OtpType.PHONE_VERIFICATION, "+10000000000");

        verify(repo).invalidateAllByUserIdAndType(eq(id), eq(OtpType.PHONE_VERIFICATION), any(OffsetDateTime.class));
        ArgumentCaptor<OtpToken> captor = ArgumentCaptor.forClass(OtpToken.class);
        verify(repo).save(captor.capture());
        OtpToken saved = captor.getValue();
        assertEquals(id, saved.getUserId());
        assertEquals(OtpType.PHONE_VERIFICATION, saved.getType());
        assertEquals(sha256Hex("123456"), saved.getOtpHash());
        assertNotNull(saved.getExpiresAt());
        verify(publisher).publishCritical(any());
    }

    @Test
    void verifyOtp_successMarksVerifiedAndUsed() throws Exception {
        UUID id = UUID.randomUUID();
        String raw = "654321";
        String hash = sha256Hex(raw);

        OtpToken token = OtpToken.builder()
                .userId(id)
                .type(OtpType.EMAIL_VERIFICATION)
                .otpHash(hash)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(false)
                .usedAt(null)
                .build();

        when(repo.findByUserIdAndTypeAndUsedAtIsNull(id, OtpType.EMAIL_VERIFICATION)).thenReturn(Optional.of(token));

        boolean result = service.verifyOtp(id, OtpType.EMAIL_VERIFICATION, raw);

        assertTrue(result);
        assertTrue(token.isVerified());
        assertNotNull(token.getUsedAt());
        verify(repo).save(token);
    }

    @Test
    void verifyOtp_throwsWhenExpired() throws Exception {
        UUID id = UUID.randomUUID();
        String raw = "111111";
        String hash = sha256Hex(raw);

        OtpToken token = OtpToken.builder()
                .userId(id)
                .type(OtpType.EMAIL_VERIFICATION)
                .otpHash(hash)
                .expiresAt(OffsetDateTime.now().minusMinutes(10))
                .verified(false)
                .usedAt(null)
                .build();

        when(repo.findByUserIdAndTypeAndUsedAtIsNull(id, OtpType.EMAIL_VERIFICATION)).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyOtp(id, OtpType.EMAIL_VERIFICATION, raw));
    }

    @Test
    void verifyOtp_throwsWhenUsed() throws Exception {
        UUID id = UUID.randomUUID();
        String raw = "222222";
        String hash = sha256Hex(raw);

        OtpToken token = OtpToken.builder()
                .userId(id)
                .type(OtpType.EMAIL_VERIFICATION)
                .otpHash(hash)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(false)
                .usedAt(OffsetDateTime.now())
                .build();

        when(repo.findByUserIdAndTypeAndUsedAtIsNull(id, OtpType.EMAIL_VERIFICATION)).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyOtp(id, OtpType.EMAIL_VERIFICATION, raw));
    }

    @Test
    void verifyOtp_throwsWhenVerified() throws Exception {
        UUID id = UUID.randomUUID();
        String raw = "333333";
        String hash = sha256Hex(raw);

        OtpToken token = OtpToken.builder()
                .userId(id)
                .type(OtpType.EMAIL_VERIFICATION)
                .otpHash(hash)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(true)
                .usedAt(null)
                .build();

        when(repo.findByUserIdAndTypeAndUsedAtIsNull(id, OtpType.EMAIL_VERIFICATION)).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> service.verifyOtp(id, OtpType.EMAIL_VERIFICATION, raw));
    }

    @Test
    void verifyOtp_throwsWhenOtpMismatch() throws Exception {
        UUID id = UUID.randomUUID();
        String correct = "444444";
        String wrong = "999999";
        String hash = sha256Hex(correct);

        OtpToken token = OtpToken.builder()
                .userId(id)
                .type(OtpType.EMAIL_VERIFICATION)
                .otpHash(hash)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .verified(false)
                .usedAt(null)
                .build();

        when(repo.findByUserIdAndTypeAndUsedAtIsNull(id, OtpType.EMAIL_VERIFICATION)).thenReturn(Optional.of(token));

        assertThrows(InvalidOtpException.class, () -> service.verifyOtp(id, OtpType.EMAIL_VERIFICATION, wrong));
    }
}
