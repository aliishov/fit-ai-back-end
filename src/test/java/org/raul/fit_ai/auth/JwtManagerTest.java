package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.service.jwt.JwtInspector;
import org.raul.fit_ai.auth.service.jwt.JwtIssuer;
import org.raul.fit_ai.auth.service.jwt.JwtManager;

import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtManagerTest {

    private JwtManager jwtManager;

    @Mock
    private JwtIssuer generateService;

    @Mock
    private JwtInspector validateService;

    @Mock
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtManager = new JwtManager(generateService, validateService);
    }

    @Test
    void generateAccessTokenDelegatesToIssuer() {
        when(generateService.generateAccessToken(principal)).thenReturn("access-token");

        String token = jwtManager.generateAccessToken(principal);

        assertEquals("access-token", token);
        verify(generateService).generateAccessToken(principal);
    }

    @Test
    void generateRefreshTokenDelegatesToIssuer() {
        when(generateService.generateRefreshToken(principal)).thenReturn("refresh-token");

        String token = jwtManager.generateRefreshToken(principal);

        assertEquals("refresh-token", token);
        verify(generateService).generateRefreshToken(principal);
    }

    @Test
    void rotateRefreshTokenValidatesOldClaimsAndCallsIssuer() {
        String oldToken = "old";
        Claims claims = mock(Claims.class);
        when(validateService.extractAllClaims(oldToken)).thenReturn(claims);
        when(generateService.rotateRefreshToken(principal, oldToken, claims)).thenReturn("rotated-token");

        String rotated = jwtManager.rotateRefreshToken(principal, oldToken);

        assertEquals("rotated-token", rotated);
        verify(validateService).extractAllClaims(oldToken);
        verify(generateService).rotateRefreshToken(principal, oldToken, claims);
    }

    @Test
    void isTokenValidDelegatesToInspector() {
        when(validateService.isTokenValid("t", principal)).thenReturn(true);

        assertTrue(jwtManager.isTokenValid("t", principal));
        verify(validateService).isTokenValid("t", principal);
    }

    @Test
    void revokeTokenDelegatesToInspector() {
        jwtManager.revokeToken("tkn");
        verify(validateService).revokeToken("tkn");
    }

    @Test
    void extractorsDelegateToInspector() {
        UUID id = UUID.randomUUID();
        when(validateService.extractUserId("tkn")).thenReturn(id);
        when(validateService.extractUsername("tkn")).thenReturn("user123");
        when(validateService.extractUserType("tkn")).thenReturn("APP_USER");

        assertEquals(id, jwtManager.extractUserId("tkn"));
        assertEquals("user123", jwtManager.extractUsername("tkn"));
        assertEquals("APP_USER", jwtManager.extractUserType("tkn"));

        verify(validateService).extractUserId("tkn");
        verify(validateService).extractUsername("tkn");
        verify(validateService).extractUserType("tkn");
    }
}
