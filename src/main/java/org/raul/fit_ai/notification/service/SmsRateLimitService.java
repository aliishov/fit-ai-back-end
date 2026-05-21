package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.common.exception.SmsLimitException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SmsRateLimitService {

    static String KEY_PREFIX = "sms_limit:";
    static Duration COOLDOWN = Duration.ofSeconds(30);

    StringRedisTemplate redisTemplate;

    public void validateRateLimit(String phone) {
        String key = KEY_PREFIX + phone;

        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", COOLDOWN);

        if (Boolean.FALSE.equals(isNew)) {
            throw new SmsLimitException(
                    "SMS already sent to this number. Please wait 30 seconds before retrying.");
        }
    }
}
