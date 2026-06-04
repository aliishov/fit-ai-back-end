package org.raul.fit_ai.auth.service.jwt;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.util.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.raul.fit_ai.auth.model.enumerated.TokenType.ACCESS_TOKEN;
import static org.raul.fit_ai.auth.model.enumerated.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtIssuer {

	JwtProperties jwtProperties;
	JwtRegistry jwtRegistry;

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(UserPrincipal principal) {
		String userIdStr = principal.getId().toString();
		String jti = UUID.randomUUID().toString();
		Long expirationMs = jwtProperties.getAccessTokenExpiration();

		List<String> authorities = principal.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userIdStr);
		claims.put("role", authorities);
		claims.put("userType", principal.getRole().name());
		claims.put("enabled", principal.isEnabled());
		claims.put("jti", jti);
		claims.put("type", "access");

		jwtRegistry.saveToken(ACCESS_TOKEN.name(), jti, userIdStr, expirationMs);

		return buildToken(claims, principal, expirationMs);
	}

	public String generateRefreshToken(UserPrincipal principal) {

		String userIdStr = principal.getId().toString();
		String jti = UUID.randomUUID().toString();
		Long expirationMs = jwtProperties.getRefreshTokenExpiration();

		Map<String, Object> claims = Map.of(
				"userId", userIdStr,
				"jti", jti,
				"type", "refresh");

		jwtRegistry.saveToken(REFRESH_TOKEN.name(), jti, userIdStr, expirationMs);

		return buildToken(claims, principal, expirationMs);
	}

	public String rotateRefreshToken(UserPrincipal principal, String oldToken, Claims oldClaims) {
		String userIdStr = principal.getId().toString();
		String jti = UUID.randomUUID().toString();
		Long expirationMs = jwtProperties.getRefreshTokenExpiration();

		String oldJti = oldClaims.get("jti", String.class);
		long oldExp = oldClaims.getExpiration().getTime();
		jwtRegistry.revokeToken(oldJti, oldExp, REFRESH_TOKEN.name());

		Map<String, Object> claims = Map.of(
				"userId", userIdStr,
				"jti", jti,
				"type", "refresh");

		jwtRegistry.saveToken(REFRESH_TOKEN.name(), jti, userIdStr, expirationMs);

		return buildToken(claims, principal, expirationMs);
	}

	private String buildToken(
			Map<String, Object> claims,
			UserPrincipal principal,
			long expirationMs
	) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(principal.getUsername())
				.issuedAt(now)
				.expiration(expiration)
				.and()
				.signWith(getSignInKey(), Jwts.SIG.HS256)
				.compact();
	}

}
