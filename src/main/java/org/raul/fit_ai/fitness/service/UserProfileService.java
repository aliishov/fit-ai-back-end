package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.common.exception.BadRequestException;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ProfileResponseDTO;
import org.raul.fit_ai.fitness.mapper.UserProfileMapper;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.repository.UserProfileRepository;
import org.raul.fit_ai.fitness.validator.UserProfileValidator;
import org.raul.fit_ai.fitness.validator.UserProfileValidator.NormalizedUserProfile;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserProfileService {

	private final UserProfileRepository userProfileRepository;

	public boolean existsByUserId(UUID userId) {
		UserProfileValidator.validateUserId(userId);
		log.info("Checking if profile exists");
		return userProfileRepository.existsByUserId(userId);
	}

	@Transactional
	public ProfileResponseDTO createOrUpdateProfile(UUID userId, ProfileRequestDTO request) {
		NormalizedUserProfile normalizedRequest = UserProfileValidator.validateAndNormalize(userId, request);
		log.info("Creating or updating profile");

		UserProfile profile = userProfileRepository.findByUserId(userId)
				.map(existing -> {
					UserProfileMapper.updateEntity(existing, normalizedRequest);
					return existing;
				})
				.orElseGet(() ->
						UserProfileMapper.toEntity(normalizedRequest)
				);

		profile = userProfileRepository.save(profile);
		return UserProfileMapper.toResponseDto(profile);
	}

	public boolean hasCompleteProfile(UUID userId) {
		UserProfileValidator.validateUserId(userId);
		return userProfileRepository.findByUserId(userId)
				.map(UserProfileValidator::isComplete)
				.orElse(false);
	}

	public UserProfile findCompleteByUserId(UUID userId) {
		UserProfileValidator.validateUserId(userId);
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new BadRequestException("Complete profile is required before workout generation"));
		if (!UserProfileValidator.isComplete(profile)) {
			throw new BadRequestException("Complete profile is required before workout generation");
		}
		return profile;
	}

	public ProfileResponseDTO getProfile(UUID userId) {
		return UserProfileMapper.toResponseDto(findByUserId(userId));
	}

	public UserProfile findByUserId(UUID userId) {
		UserProfileValidator.validateUserId(userId);
		return userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new EntityNotFoundException("Profile not found"));
	}
}
