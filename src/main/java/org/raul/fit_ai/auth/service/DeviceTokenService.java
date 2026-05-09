package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.DeviceTokenRequestDTO;
import org.raul.fit_ai.auth.mapper.DeviceTokenMapper;
import org.raul.fit_ai.auth.model.DeviceToken;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.repository.DeviceTokenRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeviceTokenService {

	DeviceTokenRepository deviceTokenRepository;
	DeviceTokenMapper deviceTokenMapper;

	@Transactional
	public void saveToken(UserPrincipal principal, DeviceTokenRequestDTO request) {
		log.info("Saving device token for principal [{}]", principal.getId());

		DeviceToken deviceToken = deviceTokenRepository
				.findByUserId(principal.getId())
				.map(existing -> {
					existing.setToken(request.token());
					existing.setPlatform(request.platform());
					return existing;
				})
				.orElseGet(() -> deviceTokenMapper.toEntity(request, principal.getId()));

		deviceTokenRepository.save(deviceToken);
	}
}
