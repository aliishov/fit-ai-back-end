package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.AdminUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {

	boolean existsByIdAndEnabledTrue(UUID id);
	boolean existsByEmail(String email);

	@Query("SELECT u FROM AdminUser u WHERE u.email = :identifier OR u.phone = :identifier")
	Optional<AdminUser> findByIdentifier(@Param("identifier") String identifier);

	@Query("SELECT COUNT(u) > 0 FROM AdminUser u WHERE u.email = :identifier OR u.phone = :identifier")
	boolean existsByIdentifier(@Param("identifier") String identifier);

	@Query("SELECT u.id FROM AdminUser u WHERE u.email = :identifier OR u.phone = :identifier")
	UUID getIdByIdentifier(String identifier);
}
