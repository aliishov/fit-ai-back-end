package org.raul.fit_ai.auth.util;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class JwtProperties {

	@NotBlank
	String secret;

	@NotNull
	Long accessTokenExpiration;

	@NotNull
	Long refreshTokenExpiration;
}
