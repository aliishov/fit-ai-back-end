package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.OtpToken;
import org.raul.fit_ai.auth.model.enumerated.OtpType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

	Optional<OtpToken> findByUserIdAndTypeAndUsedAtIsNull(UUID userId, OtpType type);

	@Modifying
	@Query("UPDATE OtpToken t SET t.usedAt = :now WHERE t.userId = :userId AND t.type = :type AND t.usedAt IS NULL")
	void invalidateAllByUserIdAndType(@Param("userId") UUID userId,
	                                  @Param("type") OtpType type,
	                                  @Param("now") OffsetDateTime now);
}
