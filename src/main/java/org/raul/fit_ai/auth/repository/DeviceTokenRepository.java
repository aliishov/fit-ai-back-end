package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.DeviceToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
	Optional<DeviceToken> findByUserId(UUID userId);
}
