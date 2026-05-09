package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

	boolean existsByEmail(String email);

	@Query("SELECT u FROM AppUser u WHERE u.email = :identifier OR u.phone = :identifier")
	Optional<AppUser> findByIdentifier(@Param("identifier") String identifier);

	@Query("SELECT COUNT(u) > 0 FROM AppUser u WHERE u.email = :identifier OR u.phone = :identifier")
	boolean existsByIdentifier(@Param("identifier") String identifier);

	@Query("SELECT u.id FROM AppUser u WHERE u.email = :identifier OR u.phone = :identifier")
	UUID getIdByIdentifier(String identifier);
}
