package org.raul.fit_ai.auth.service.jwt;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.util.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.UUID;
import java.util.function.Function;

import static org.raul.fit_ai.auth.model.enumerated.TokenType.ACCESS_TOKEN;
import static org.raul.fit_ai.auth.model.enumerated.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtInspector {

	JwtProperties jwtProperties;
	JwtRegistry jwtRegistry;

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}

	protected Claims extractAllClaims(String oldToken) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(oldToken)
				.getPayload();
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	public UUID extractUserId(String token) {
		String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
		return userIdStr != null ? UUID.fromString(userIdStr) : null;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractUserType(String token) {
		return extractClaim(token, claims -> claims.get("userType", String.class));
	}

	public String extractJti(String token) {
		return extractClaim(token, claims -> claims.get("jti", String.class));
	}

	public Boolean isTokenValid(String token, UserPrincipal principal) {
		try {
			Claims claims = extractAllClaims(token);
			String username = claims.getSubject();
			String jti = claims.get("jti", String.class);

			if (username == null || !username.equals(principal.getUsername())) {
				log.warn("JWT subject mismatch");
				return false;
			}

			if (jwtRegistry.isTokenRevoked(jti)) {
				log.warn("JWT token is revoked");
				return false;
			}

			return true;

		} catch (ExpiredJwtException e) {
			log.warn("JWT token expired");
			return false;
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("JWT validation failed type=[{}]", e.getClass().getSimpleName());
			return false;
		}
	}

	public void revokeToken(String token) {
		Claims claims = extractAllClaims(token);
		String jti = claims.get("jti", String.class);
		long exp = claims.getExpiration().getTime();
		String type = claims.get("type", String.class);

		String tokenTypeName = "access".equals(type)
				? ACCESS_TOKEN.name()
				: REFRESH_TOKEN.name();

		jwtRegistry.revokeToken(jti, exp, tokenTypeName);
	}
}
