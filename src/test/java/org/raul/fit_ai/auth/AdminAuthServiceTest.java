package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.mapper.AdminUserMapper;
import org.raul.fit_ai.auth.model.AdminUser;
import org.raul.fit_ai.auth.repository.AdminUserRepository;
import org.raul.fit_ai.auth.service.AdminAuthService;
import org.raul.fit_ai.auth.service.PasswordManagementService;
import org.raul.fit_ai.auth.service.PasswordResetTokenService;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.common.exception.UnauthorizedException;
import org.raul.fit_ai.common.services.NotificationPublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationProvider;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthServiceTest {

    @Mock
    private AdminUserRepository userRepository;

    @Mock
    private AdminUserMapper adminUserMapper;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private JwtManager jwtManager;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private PasswordManagementService passwordManagementService;

    @Mock
    private NotificationPublisher notificationPublisher;

    private AdminAuthService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AdminAuthService(
                userRepository, adminUserMapper, authenticationProvider, jwtManager,
                passwordResetTokenService, passwordManagementService, notificationPublisher
        );
    }

    @Test
    void createAdmin_createsAdminIfCreatorIsActiveAdmin() {
        UUID creatorId = UUID.randomUUID();
        UUID newAdminId = UUID.randomUUID();
        RegisterRequestDTO request = new RegisterRequestDTO("admin@example.com", "password123", "Admin", "User");

        AdminUser newAdmin = AdminUser.builder().id(newAdminId).email("admin@example.com").build();

        when(userRepository.existsByIdAndEnabledTrue(creatorId)).thenReturn(true);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(adminUserMapper.toEntity(creatorId, request)).thenReturn(newAdmin);
        when(userRepository.save(newAdmin)).thenReturn(newAdmin);

        var uri = service.createAdmin(creatorId, request);

        assertNotNull(uri);
        assertTrue(uri.toString().contains(newAdminId.toString()));
        verify(userRepository).save(newAdmin);
        verify(notificationPublisher).publish(any());
    }

    @Test
    void createAdmin_throwsWhenCreatorIsNotActiveAdmin() {
        UUID creatorId = UUID.randomUUID();
        RegisterRequestDTO request = new RegisterRequestDTO("admin@example.com", "password123", "Admin", "User");

        when(userRepository.existsByIdAndEnabledTrue(creatorId)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> service.createAdmin(creatorId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createAdmin_throwsWhenEmailExists() {
        UUID creatorId = UUID.randomUUID();
        RegisterRequestDTO request = new RegisterRequestDTO("existing@example.com", "password123", "Admin", "User");

        when(userRepository.existsByIdAndEnabledTrue(creatorId)).thenReturn(true);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createAdmin(creatorId, request));
        verify(userRepository, never()).save(any());
    }
}
