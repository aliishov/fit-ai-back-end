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
		log.info("Recording progress");

		UserProgress userProgress = UserProgressMapper.toEntity(normalizedRequest);
		userProgress = userProgressRepository.save(userProgress);

		return UserProgressMapper.toResponseDto(userProgress);
	}

	public List<ProgressResponseDTO> getProgress(UserPrincipal principal, UUID planId) {
		UUID userId = progressValidator.requireUserId(principal);
		progressValidator.validatePlanForProgressRead(planId, userId);
		log.info("Getting progress for plan [{}]", planId);

		return userProgressRepository.findByUserIdAndPlanIdOrderByRecordedAtDesc(userId, planId)
				.stream()
				.map(UserProgressMapper::toResponseDto)
				.toList();
	}

	public List<ProgressResponseDTO> getProgressHistory(UserPrincipal principal) {
		UUID userId = progressValidator.requireUserId(principal);
		log.info("Getting progress history");

		return userProgressRepository.findByUserIdOrderByRecordedAtDesc(userId)
				.stream()
				.map(UserProgressMapper::toResponseDto)
				.toList();
	}

	public ProgressResponseDTO getLatestProgress(UserPrincipal principal) {
		UUID userId = progressValidator.requireUserId(principal);
		log.info("Getting latest progress");

		return userProgressRepository.findFirstByUserIdOrderByRecordedAtDesc(userId)
				.map(UserProgressMapper::toResponseDto)
				.orElseThrow(() -> new EntityNotFoundException("Progress not found"));
	}

	public ProgressResponseDTO getProgressRecord(UserPrincipal principal, UUID progressId) {
		UUID userId = progressValidator.requireUserId(principal);
		progressValidator.validateProgressId(progressId);
		log.info("Getting progress record [{}]", progressId);

		return userProgressRepository.findByIdAndUserId(progressId, userId)
				.map(UserProgressMapper::toResponseDto)
				.orElseThrow(() -> new EntityNotFoundException("Progress not found"));
	}
}
