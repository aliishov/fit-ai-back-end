package org.raul.fit_ai.auth;

import org.raul.fit_ai.auth.service.SignOutHandler;
import org.raul.fit_ai.auth.service.jwt.JwtManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

class SignOutHandlerTest {

    @Mock
    private JwtManager jwtManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private SignOutHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new SignOutHandler(jwtManager);
    }

    @Test
    void logout_extractsTokenFromBearerAuthHeaderAndRevokes() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-xyz-123");

        handler.logout(request, response, authentication);

        verify(jwtManager).revokeToken("token-xyz-123");
    }

    @Test
    void logout_ignoresRequestWithoutAuthorizationHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        handler.logout(request, response, authentication);

        verifyNoInteractions(jwtManager);
    }

    @Test
    void logout_ignoresRequestWithoutBearerPrefix() {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");

        handler.logout(request, response, authentication);

        verifyNoInteractions(jwtManager);
    }

    @Test
    void logout_acceptsNullAuthentication() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-abc");

        handler.logout(request, response, null);

        verify(jwtManager).revokeToken("token-abc");
    }
}
