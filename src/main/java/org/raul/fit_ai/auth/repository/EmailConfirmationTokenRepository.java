package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.EmailVerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	@Modifying
	@Query("UPDATE EmailVerificationToken t SET t.usedAt = :now WHERE t.userId = :userId AND t.usedAt IS NULL")
	void invalidateAllByUserId(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

	Optional<EmailVerificationToken> findByUserIdAndUsedAtIsNull(UUID userId);
}
