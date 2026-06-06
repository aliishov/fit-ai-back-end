package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.dto.request.ChangePasswordRequestDTO;
import org.raul.fit_ai.auth.dto.request.UpdateProfileRequestDTO;
import org.raul.fit_ai.auth.model.AdminUser;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.repository.AdminUserRepository;
import org.raul.fit_ai.auth.repository.AppUserRepository;
import org.raul.fit_ai.auth.service.AccountService;
import org.raul.fit_ai.auth.service.PasswordManagementService;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.services.NotificationPublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AdminUserRepository adminRepo;

    @Mock
    private AppUserRepository appRepo;

    @Mock
    private PasswordManagementService passwordService;

    @Mock
    private JwtManager jwtManager;

    @Mock
    private NotificationPublisher publisher;

    private AccountService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AccountService(adminRepo, appRepo, passwordService, jwtManager, publisher);
    }

    @Test
    void getProfile_returnsDtoFromPrincipalUser() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .firstName("F")
                .lastName("L")
                .email("e@x.com")
                .phone("+100")
                .avatarUrl("/a.png")
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        var dto = service.getProfile(principal);

        assertEquals(user.getId(), dto.id());
        assertEquals("F", dto.firstName());
        assertEquals("L", dto.lastName());
        assertEquals("e@x.com", dto.email());
        assertEquals("+100", dto.phone());
        assertEquals("/a.png", dto.avatarUrl());
    }

    @Test
    void updateProfile_appUser_updatesFieldsAndSavesWithPhoneVerificationFalse() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .phoneVerified(null)
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        UpdateProfileRequestDTO req = new UpdateProfileRequestDTO("John", "Doe", "+123456789");

        service.updateProfile(principal, req);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("+123456789", user.getPhone());
        verify(appRepo).save(user);
	    assertNotEquals(Boolean.TRUE, user.getPhoneVerified());
    }

    @Test
    void updateProfile_adminUser_callsAdminRepoSave() {
        AdminUser admin = AdminUser.builder()
                .id(UUID.randomUUID())
                .build();

        UserPrincipal principal = new UserPrincipal(admin);

        UpdateProfileRequestDTO req = new UpdateProfileRequestDTO("A", "B", null);

        service.updateProfile(principal, req);

        assertEquals("A", admin.getFirstName());
        assertEquals("B", admin.getLastName());
        verify(adminRepo).save(admin);
    }

    @Test
    void updatePassword_successFlow_callsPasswordServiceSavesPublishesAndRevokesToken() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .passwordHash("encoded-old")
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO("oldPlain", "newPlain", "refresh-token-123");

        // ensure mocked passwordService doesn't throw for the happy path
        doNothing().when(passwordService).isPasswordMatches("oldPlain", "encoded-old");
        doNothing().when(passwordService).validateNewPassword("oldPlain", "newPlain");
        doNothing().when(passwordService).updatePassword(user, "newPlain");

        service.updatePassword(principal, req);

        verify(passwordService).isPasswordMatches("oldPlain", "encoded-old");
        verify(passwordService).validateNewPassword("oldPlain", "newPlain");
        verify(passwordService).updatePassword(user, "newPlain");

        verify(appRepo).save(user);
	    assertEquals(Boolean.TRUE, user.getPhoneVerified());

        verify(publisher).publishCritical(any());
        verify(jwtManager).revokeToken("refresh-token-123");
    }
}
