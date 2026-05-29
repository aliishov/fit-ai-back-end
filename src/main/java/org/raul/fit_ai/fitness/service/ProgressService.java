package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ProgressResponseDTO;
import org.raul.fit_ai.fitness.mapper.UserProgressMapper;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.raul.fit_ai.fitness.repository.UserProgressRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

	private final UserProgressRepository userProgressRepository;
	private final WorkoutService workoutService;

	@Transactional
	public void recordProgress(UserPrincipal principal, RecordProgressRequestDTO request) {
		log.info("Recording progress for principal [{}]", principal.getId());

		if (!workoutService.existsById(request.planId())) {
			throw new IllegalArgumentException("Workout does not exist");
		}

		if (!workoutService.isPLanActive(request.planId())) {
			throw new IllegalArgumentException("Workout is not active");
		}

		UserProgress userProgress = UserProgressMapper.toEntity(request, principal.getId());
		userProgressRepository.save(userProgress);
	}

	public List<ProgressResponseDTO> getProgress(UserPrincipal principal, UUID planId) {
		log.info("Getting progress for plan [{}] and for principal [{}]", planId, principal.getId());

		List<UserProgress> userProgresses = userProgressRepository.findByUserIdAndPlanId(principal.getId(), planId);

		return userProgresses.stream()
				.map(UserProgressMapper::toResponseDto)
				.toList();
	}

	public List<UserProgress> findByUserIdOrderByRecordedAtDesc(UUID userId) {
		return userProgressRepository.findByUserIdOrderByRecordedAtDesc(userId);
	}
}
