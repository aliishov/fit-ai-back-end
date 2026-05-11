package org.raul.fit_ai.auth.service.jwt;

import org.raul.fit_ai.auth.model.UserPrincipal;

import io.jsonwebtoken.Claims;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtManager {

	JwtIssuer generateService;
	JwtInspector validateService;

	public String generateAccessToken(UserPrincipal principal) {
		return generateService.generateAccessToken(principal);
	}

	public String generateRefreshToken(UserPrincipal principal) {
		return generateService.generateRefreshToken(principal);
	}

	public String rotateRefreshToken(UserPrincipal principal, String oldToken) {
		Claims oldClaims = validateService.extractAllClaims(oldToken);
		return generateService.rotateRefreshToken(principal, oldToken, oldClaims);
	}

	public boolean isTokenValid(String token, UserPrincipal principal) {
		return validateService.isTokenValid(token, principal);
	}

	public void revokeToken(String token) {
		validateService.revokeToken(token);
	}

	public UUID extractUserId(String token) {
		return validateService.extractUserId(token);
	}

	public String extractUsername(String token) {
		return validateService.extractUsername(token);
	}

	public String extractUserType(String token) {
		return validateService.extractUserType(token);
	}
}
