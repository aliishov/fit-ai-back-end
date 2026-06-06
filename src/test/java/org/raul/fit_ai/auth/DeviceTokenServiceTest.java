package org.raul.fit_ai.auth;

import org.mockito.Mock;
import org.raul.fit_ai.auth.dto.request.DeviceTokenRequestDTO;
import org.raul.fit_ai.auth.model.DeviceToken;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.model.enumerated.DevicePlatform;
import org.raul.fit_ai.auth.repository.DeviceTokenRepository;
import org.raul.fit_ai.auth.service.DeviceTokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceTokenServiceTest {

    @Mock
    private DeviceTokenRepository repo;

    @Mock
    private DeviceTokenService service;

    @BeforeEach
    void setUp() {
        repo = mock(DeviceTokenRepository.class);
        service = new DeviceTokenService(repo);
    }

    @Test
    void saveToken_updatesExistingDeviceToken() {
        UUID userId = UUID.randomUUID();
        DeviceToken existing = DeviceToken.builder()
                .userId(userId)
                .token("old-token")
                .platform(DevicePlatform.ANDROID)
                .build();

        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(userId);

        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO("new-token", DevicePlatform.IOS);

        when(repo.findByUserId(userId)).thenReturn(Optional.of(existing));

        service.saveToken(principal, request);

        assertEquals("new-token", existing.getToken());
        assertEquals(DevicePlatform.IOS, existing.getPlatform());
        verify(repo).save(existing);
    }

    @Test
    void saveToken_createsAndSavesNewDeviceTokenWhenNotFound() {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(userId);

        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO("token-abc", DevicePlatform.ANDROID);

        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        service.saveToken(principal, request);

        ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
        verify(repo).save(captor.capture());
        DeviceToken saved = captor.getValue();

        assertEquals(userId, saved.getUserId());
        assertEquals("token-abc", saved.getToken());
        assertEquals(DevicePlatform.ANDROID, saved.getPlatform());
    }

    @Test
    void findActiveTokensByUserId_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        when(repo.findActiveTokensByUserId(userId)).thenReturn(List.of("t1", "t2"));

        var tokens = service.findActiveTokensByUserId(userId);

        assertEquals(2, tokens.size());
        assertEquals(List.of("t1", "t2"), tokens);
    }

    @Test
    void deactivateByToken_callsRepository() {
        service.deactivateByToken("abc-token");

        verify(repo).deactivateByToken("abc-token");
    }
}
