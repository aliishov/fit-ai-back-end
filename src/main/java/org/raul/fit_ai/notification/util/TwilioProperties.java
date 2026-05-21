package org.raul.fit_ai.notification.util;

import jakarta.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "twilio")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class TwilioProperties {

	@NotBlank
	String accountSid;

	@NotBlank
	String authToken;

	@NotBlank
	String phoneNumber;
}
