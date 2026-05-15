package org.raul.fit_ai.auth.repository;

import org.raul.fit_ai.auth.model.EmailVerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
}
