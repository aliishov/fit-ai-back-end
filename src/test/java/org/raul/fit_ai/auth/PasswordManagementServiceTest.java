package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.service.PasswordManagementService;
import org.raul.fit_ai.common.exception.PasswordsDoNotMatchException;

import jakarta.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordManagementServiceTest {

    private PasswordManagementService service;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private BaseUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PasswordManagementService(encoder);
    }

    @Test
    void validatePasswordMatch_noThrow_whenEqual() {
        assertDoesNotThrow(() -> service.validatePasswordMatch("p", "p"));
    }

    @Test
    void validatePasswordMatch_throws_whenDifferent() {
        assertThrows(PasswordsDoNotMatchException.class,
                () -> service.validatePasswordMatch("a", "b"));
    }

    @Test
    void validateNewPassword_throws_whenSameAsOld() {
        assertThrows(ValidationException.class,
                () -> service.validateNewPassword("old", "old"));
    }

    @Test
    void updatePassword_setsEncodedHashOnUser() {
        when(encoder.encode("newPass")).thenReturn("encoded-new");

        service.updatePassword(user, "newPass");

        verify(user).setPasswordHash("encoded-new");
    }

    @Test
    void encodePassword_delegatesToEncoder() {
        when(encoder.encode("x")).thenReturn("y");
        assertEquals("y", service.encodePassword("x"));
        verify(encoder).encode("x");
    }

    @Test
    void isPasswordMatches_noThrow_whenMatches() {
        when(encoder.matches("in", "stored")).thenReturn(true);
        assertDoesNotThrow(() -> service.isPasswordMatches("in", "stored"));
    }

    @Test
    void isPasswordMatches_throws_whenNotMatches() {
        when(encoder.matches("in", "stored")).thenReturn(false);
        assertThrows(PasswordsDoNotMatchException.class,
                () -> service.isPasswordMatches("in", "stored"));
    }
}
