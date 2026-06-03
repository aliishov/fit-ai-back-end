package org.raul.fit_ai.notification.client;

import org.raul.fit_ai.auth.service.DeviceTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeviceTokenClient {

	private final DeviceTokenService deviceTokenService;

	public List<String> findActiveTokensByUserId(UUID userId) {
		return deviceTokenService.findActiveTokensByUserId(userId);
	}

	public void deactivateByToken(String deviceToken) {
		deviceTokenService.deactivateByToken(deviceToken);
	}
}
