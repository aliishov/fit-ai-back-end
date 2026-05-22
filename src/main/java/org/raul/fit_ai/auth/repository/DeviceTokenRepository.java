package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.DeviceToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
	Optional<DeviceToken> findByUserId(UUID userId);

	@Query("SELECT dt.token FROM DeviceToken dt WHERE dt.userId = :userId AND dt.active = true")
	List<String> findActiveTokensByUserId(@Param("userId") UUID userId);

	@Modifying
	@Query("UPDATE DeviceToken dt SET dt.active = false WHERE dt.token = :token")
	void deactivateByToken(@Param("token") String token);
}
