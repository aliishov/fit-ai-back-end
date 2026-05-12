package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.service.jwt.JwtManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SignOutHandler implements LogoutHandler {

	JwtManager jwtManager;

	@Override
	public void logout(
			@NonNull HttpServletRequest request,
	        @NonNull HttpServletResponse response,
	        @Nullable Authentication authentication
	) {
		final String authHeader = request.getHeader("Authorization");
		final String token;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

		token = authHeader.substring(7);

		jwtManager.revokeToken(token);
	}
}
