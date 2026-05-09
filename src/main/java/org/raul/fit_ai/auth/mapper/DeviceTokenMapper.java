package org.raul.fit_ai.auth.mapper;

import org.raul.fit_ai.auth.dto.request.DeviceTokenRequestDTO;
import org.raul.fit_ai.auth.model.DeviceToken;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeviceTokenMapper {

	public DeviceToken toEntity(DeviceTokenRequestDTO request, UUID userId) {
		return DeviceToken.builder()
				.userId(userId)
				.token(request.token())
				.platform(request.platform())
				.build();
	}
}
