package org.raul.fit_ai.fitness.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.fitness.dto.request.ProfileRequestDTO;
import org.raul.fit_ai.fitness.mapper.UserProfileMapper;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {

	UserProfileRepository userProfileRepository;
	UserProfileMapper userProfileMapper;

	public UserProfile findUserProfileByUserId(UUID userId) {
		return userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new EntityNotFoundException("UserProfile not found"));
	}

	@Transactional(readOnly = true)
	public boolean existsByUserId(UUID userId) {
		log.info("Checking if profile exists by user id [{}]", userId);
		return userProfileRepository.existsByUserId(userId);
	}

	@Transactional
	public UUID createProfile(UUID userId, ProfileRequestDTO request) {
		log.info("Creating profile for principal [{}]", userId);

		UserProfile profile;
		if (userProfileRepository.existsByUserId(userId)) {
			profile = userProfileMapper.toEntity(request, userId);
		} else {
			profile = findUserProfileByUserId(userId);
			updateProfile(profile, request);
		}

		profile = userProfileRepository.save(profile);
		return profile.getId();
	}

	private void updateProfile(UserProfile profile, ProfileRequestDTO request) {
		profile.setActivityType(request.activityType());
		profile.setWeightKg(request.weightKg());
		profile.setHeightCm(request.heightCm());
		profile.setAge(request.age());
		profile.setGender(request.gender());
		profile.setGoal(request.goal());
		profile.setFitnessLevel(request.fitnessLevel());
		profile.setSessionsPerWeek(request.sessionsPerWeek());
		profile.setLimitations(request.limitations());
	}
}
