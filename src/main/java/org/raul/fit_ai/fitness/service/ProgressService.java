package org.raul.fit_ai.fitness.service;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.dto.response.ProgressResponseDTO;
import org.raul.fit_ai.fitness.mapper.UserProgressMapper;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.raul.fit_ai.fitness.repository.UserProgressRepository;
import org.raul.fit_ai.fitness.validator.ProgressValidator;
import org.raul.fit_ai.fitness.validator.ProgressValidator.NormalizedProgressRecord;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProgressService {

	private final UserProgressRepository userProgressRepository;
	private final ProgressValidator progressValidator;

	@Transactional
	public ProgressResponseDTO recordProgress(UserPrincipal principal, RecordProgressRequestDTO request) {
		NormalizedProgressRecord normalizedRequest = progressValidator.validateAndNormalizeRecordRequest(
				principal,
				request
		);
		log.info("Recording progress for principal [{}]", normalizedRequest.userId());

		UserProgress userProgress = UserProgressMapper.toEntity(normalizedRequest);
		userProgress = userProgressRepository.save(userProgress);

		return UserProgressMapper.toResponseDto(userProgress);
	}

	public List<ProgressResponseDTO> getProgress(UserPrincipal principal, UUID planId) {
		UUID userId = progressValidator.requireUserId(principal);
		progressValidator.validatePlanForProgressRead(planId, userId);
		log.info("Getting progress for plan [{}] and for principal [{}]", planId, userId);

		return userProgressRepository.findByUserIdAndPlanIdOrderByRecordedAtDesc(userId, planId)
				.stream()
				.map(UserProgressMapper::toResponseDto)
				.toList();
	}

	public List<ProgressResponseDTO> getProgressHistory(UserPrincipal principal) {
		UUID userId = progressValidator.requireUserId(principal);
		log.info("Getting progress history for principal [{}]", userId);

		return userProgressRepository.findByUserIdOrderByRecordedAtDesc(userId)
				.stream()
				.map(UserProgressMapper::toResponseDto)
				.toList();
	}

	public ProgressResponseDTO getLatestProgress(UserPrincipal principal) {
		UUID userId = progressValidator.requireUserId(principal);
		log.info("Getting latest progress for principal [{}]", userId);

		return userProgressRepository.findFirstByUserIdOrderByRecordedAtDesc(userId)
				.map(UserProgressMapper::toResponseDto)
				.orElseThrow(() -> new EntityNotFoundException("Progress not found"));
	}

	public ProgressResponseDTO getProgressRecord(UserPrincipal principal, UUID progressId) {
		UUID userId = progressValidator.requireUserId(principal);
		progressValidator.validateProgressId(progressId);
		log.info("Getting progress record [{}] for principal [{}]", progressId, userId);

		return userProgressRepository.findByIdAndUserId(progressId, userId)
				.map(UserProgressMapper::toResponseDto)
				.orElseThrow(() -> new EntityNotFoundException("Progress not found"));
	}
}
