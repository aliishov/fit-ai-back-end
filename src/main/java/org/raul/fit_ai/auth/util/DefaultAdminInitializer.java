package org.raul.fit_ai.auth.util;

import org.raul.fit_ai.auth.model.AdminUser;
import org.raul.fit_ai.auth.model.enumerated.Role;
import org.raul.fit_ai.auth.repository.AdminUserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class DefaultAdminInitializer {

	final AdminUserRepository adminRepository;
	final PasswordEncoder passwordEncoder;

	@Value("${app.admin.email}")
	String adminEmail;

	@Value("${app.admin.password}")
	String adminPassword;

	@Value("${app.admin.first-name}")
	String adminFirstName;

	@Value("${app.admin.last-name}")
	String adminLastName;

	@PostConstruct
	public void init() {
		if (adminRepository.existsByEmail(adminEmail)) {
			log.info("Default admin already exists; skipping initialization");
			return;
		}

		AdminUser admin = AdminUser.builder()
				.firstName(adminFirstName)
				.lastName(adminLastName)
				.email(adminEmail)
				.passwordHash(passwordEncoder.encode(adminPassword))
				.role(Role.ROLE_ADMIN)
				.enabled(true)
				.build();

		adminRepository.save(admin);
	}
}
