package org.raul.fit_ai.auth.service.jwt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtRegistry {

	static String TOKEN_PREFIX = "jwt:";
	static String BLACKLIST_PREFIX = TOKEN_PREFIX + "blacklist:";

	RedisTemplate<String, String> redisTemplate;

	public void saveToken(String tokenType, String jti, String userId, long expiration) {
		String key = TOKEN_PREFIX + tokenType + ":" + jti;

		redisTemplate.opsForValue().set(
				key,
				userId,
				expiration,
				TimeUnit.MILLISECONDS);

		log.debug("{} with jti {} saved for user {}", tokenType, jti, userId);
	}

	public void revokeToken(String jti, long expiration, String tokenType) {
		long ttlMs = expiration - System.currentTimeMillis();
		if (ttlMs <= 0) {
			log.warn("Attempted to revoke token {} with expired TTL", jti);
			return;
		}

		String key = TOKEN_PREFIX + tokenType + ":" + jti;
		redisTemplate.delete(key);

		blacklist(jti, ttlMs);
	}

	public boolean isTokenRevoked(String jti) {
		String blacklistKey = BLACKLIST_PREFIX + jti;
		return redisTemplate.hasKey(blacklistKey);
	}

	public void revokeAllUserTokens(String userId) {
		String accessPattern = TOKEN_PREFIX + "ACCESS_TOKEN:*";
		revokeTokensByPattern(accessPattern, userId);

		String refreshPattern = TOKEN_PREFIX + "REFRESH_TOKEN:*";
		revokeTokensByPattern(refreshPattern, userId);

		log.info("Revoked all tokens for user {}", userId);
	}

	private void revokeTokensByPattern(String pattern, String userId) {
		ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

		try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
				.getConnection().scan(options)) {
			while (cursor.hasNext()) {
				String key = new String(cursor.next());
				String storedUserId = redisTemplate.opsForValue().get(key);
				if (userId.equals(storedUserId)) {
					String jti = key.substring(key.lastIndexOf(':') + 1);
					Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
					if (ttl != null && ttl > 0) {
						blacklist(jti, ttl);
					}
					redisTemplate.delete(key);
				}
			}
		}
	}

	private void blacklist(String jti, long ttlMs) {
		redisTemplate.opsForValue().set(
				BLACKLIST_PREFIX + jti,
				"revoked",
				ttlMs,
				TimeUnit.MILLISECONDS);

		log.debug("Token with jti {} revoked", jti);
	}
}
