package org.raul.fit_ai.fitness.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.fitness.dto.request.RecordProgressRequestDTO;
import org.raul.fit_ai.fitness.mapper.UserProgressMapper;
import org.raul.fit_ai.fitness.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProgressService {

	UserProgressRepository userProgressRepository;

	@Transactional
	public void recordProgress(UserPrincipal principal, RecordProgressRequestDTO request) {
		log.info("Recording progress for principal [{}]", principal.getId());

		// TODO
	}
}
