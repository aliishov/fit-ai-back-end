package org.raul.fit_ai.fitness.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

	public UserProfile findUserProfileByUserId(UUID userId) {
		return userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new EntityNotFoundException("UserProfile not found"));
	}

	@Transactional(readOnly = true)
	public boolean existsByUserId(UUID userId) {
		return userProfileRepository.existsByUserId(userId);
	}
}
