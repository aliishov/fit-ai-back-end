package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.common.exception.PasswordsDoNotMatchException;

import jakarta.validation.ValidationException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordManagementService {

	PasswordEncoder passwordEncoder;

	public void validatePasswordMatch(String password, String repeatedPassword) {
		if (!password.equals(repeatedPassword)) {
			log.warn("Password validation failed - passwords don't match");
			throw new PasswordsDoNotMatchException("Passwords don't match");
		}
	}

	public void validateNewPassword(String oldPassword, String newPassword) {
		if (oldPassword.equals(newPassword)) {
			throw new ValidationException("New password cannot be the same");
		}
	}

	public void updatePassword(BaseUser user, String newPassword) {
		String encodedPassword = encodePassword(newPassword);
		user.setPasswordHash(encodedPassword);
		log.debug("Password reset for user: {}", user.getId());
	}

	public String encodePassword(String plainPassword) {
		return passwordEncoder.encode(plainPassword);
	}

	public void isPasswordMatches(String inputPassword, String userPassword) {
		if (!passwordEncoder.matches(inputPassword, userPassword)) {
			throw new PasswordsDoNotMatchException("Old password doesn't match");
		}
	}
}
