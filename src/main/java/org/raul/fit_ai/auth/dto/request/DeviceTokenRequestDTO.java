package org.raul.fit_ai.auth.dto.request;

import org.raul.fit_ai.auth.model.enumerated.DevicePlatform;

public record DeviceTokenRequestDTO(
		String token,
		DevicePlatform platform
) {
}
