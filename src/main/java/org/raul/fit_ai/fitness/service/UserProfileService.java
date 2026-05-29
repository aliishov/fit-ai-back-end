package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.mapper.UserProfileMapper;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.repository.UserProfileRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {

	UserProfileRepository userProfileRepository;

	@Transactional(readOnly = true)
	public boolean existsByUserId(UUID userId) {
		log.info("Checking if profile exists by user id [{}]", userId);
		return userProfileRepository.existsByUserId(userId);
	}

	@Transactional
	public void createOrUpdateProfile(UUID userId, ProfileRequestDTO request) {
		log.info("Creating or updating profile for user [{}]", userId);

		UserProfile profile = userProfileRepository.findByUserId(userId)
				.map(existing -> {
					updateProfile(existing, request);
					return existing;
				})
				.orElseGet(() ->
						UserProfileMapper.toEntity(request, userId)
				);

		userProfileRepository.save(profile);
	}

	private void updateProfile(UserProfile profile, ProfileRequestDTO request) {
		if (request.activityType() != null) profile.setActivityType(request.activityType());
		if (request.weightKg() != null) profile.setWeightKg(request.weightKg());
		if (request.heightCm() != null) profile.setHeightCm(request.heightCm());
		if (request.age() != null) profile.setAge(request.age());
		if (request.gender() != null) profile.setGender(request.gender());
		if (request.goal() != null) profile.setGoal(request.goal());
		if (request.fitnessLevel() != null) profile.setFitnessLevel(request.fitnessLevel());
		if (request.sessionsPerWeek() != null) profile.setSessionsPerWeek(request.sessionsPerWeek());
		if (request.limitations() != null) profile.setLimitations(request.limitations());
	}

	@Transactional(readOnly = true)
	public UserProfile findByUserId(UUID userId) {
		return userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new EntityNotFoundException("Profile not found"));
	}
}
