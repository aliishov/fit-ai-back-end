package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.DeviceTokenRequestDTO;
import org.raul.fit_ai.auth.mapper.DeviceTokenMapper;
import org.raul.fit_ai.auth.model.DeviceToken;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.repository.DeviceTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

	private final DeviceTokenRepository deviceTokenRepository;

	@Transactional
	public void saveToken(UserPrincipal principal, DeviceTokenRequestDTO request) {
		DeviceToken deviceToken = deviceTokenRepository
				.findByUserId(principal.getId())
				.map(existing -> {
					existing.setToken(request.token());
					existing.setPlatform(request.platform());
					return existing;
				})
				.orElseGet(() -> DeviceTokenMapper.toEntity(request, principal.getId()));

		deviceTokenRepository.save(deviceToken);
	}

	@Transactional(readOnly = true)
	public List<String> findActiveTokensByUserId(UUID userId) {
		return deviceTokenRepository.findActiveTokensByUserId(userId);
	}

	@Transactional()
	public void deactivateByToken(String deviceToken) {
		deviceTokenRepository.deactivateByToken(deviceToken);
	}
}
