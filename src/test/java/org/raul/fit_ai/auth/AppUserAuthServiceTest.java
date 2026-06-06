package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.dto.request.EmailConfirmRequestDTO;
import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.mapper.AppUserMapper;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.model.enumerated.OtpType;
import org.raul.fit_ai.auth.repository.AppUserRepository;
import org.raul.fit_ai.auth.service.AppUserAuthService;
import org.raul.fit_ai.auth.service.OtpService;
import org.raul.fit_ai.auth.service.PasswordManagementService;
import org.raul.fit_ai.auth.service.PasswordResetTokenService;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.exception.DuplicateResourceException;
import org.raul.fit_ai.common.services.NotificationPublisher;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserAuthServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private AppUserMapper appUserMapper;

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

    @Mock
    private OtpService otpService;

    private AppUserAuthService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AppUserAuthService(
                userRepository, appUserMapper, authenticationProvider, jwtManager,
                passwordResetTokenService, passwordManagementService, notificationPublisher, otpService
        );
    }

    @Test
    void signUp_createsUserAndPublishesNotification() {
        UUID userId = UUID.randomUUID();
        RegisterRequestDTO request = new RegisterRequestDTO("test@example.com", "password123", "John", "Doe");

        AppUser appUser = AppUser.builder().id(userId).email("test@example.com").build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(appUserMapper.toEntity(request)).thenReturn(appUser);
        when(userRepository.save(appUser)).thenReturn(appUser);

        var uri = service.signUp(request);

        assertNotNull(uri);
        assertTrue(uri.toString().contains(userId.toString()));
        verify(userRepository).save(appUser);
        verify(notificationPublisher).publish(any());
    }

    @Test
    void signUp_throwsWhenEmailExists() {
        RegisterRequestDTO request = new RegisterRequestDTO("existing@example.com", "password123", "John", "Doe");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.signUp(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void sendEmailConfirmation_generatesOtpForUser() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).email("test@example.com").build();
        UserPrincipal principal = new UserPrincipal(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.sendEmailConfirmation(principal);

        verify(otpService).generateOtp(user, OtpType.EMAIL_VERIFICATION, "test@example.com");
    }

    @Test
    void sendEmailConfirmation_throwsWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).build();
        UserPrincipal principal = new UserPrincipal(user);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.sendEmailConfirmation(principal));
    }

    @Test
    void emailConfirm_marksEmailAsVerified() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).email("test@example.com").build();
        UserPrincipal principal = new UserPrincipal(user);
        EmailConfirmRequestDTO request = new EmailConfirmRequestDTO("123456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpService.verifyOtp(userId, OtpType.EMAIL_VERIFICATION, "123456")).thenReturn(true);

        service.emailConfirm(principal, request);

        assertTrue(user.isEmailVerified());
        verify(userRepository).save(user);
    }

    @Test
    void sendPhoneConfirmation_generatesOtpForUser() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).phone("+10000000000").build();
        UserPrincipal principal = new UserPrincipal(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.sendPhoneConfirmation(principal);

        verify(otpService).generateOtp(user, OtpType.PHONE_VERIFICATION, "+10000000000");
    }

    @Test
    void phoneConfirm_marksPhoneAsVerified() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).build();
        UserPrincipal principal = new UserPrincipal(user);
        EmailConfirmRequestDTO request = new EmailConfirmRequestDTO("654321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpService.verifyOtp(userId, OtpType.PHONE_VERIFICATION, "654321")).thenReturn(true);

        service.phoneConfirm(principal, request);

        assertTrue(user.isPhoneVerified());
        verify(userRepository).save(user);
    }

    @Test
    void findById_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var result = service.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
}
