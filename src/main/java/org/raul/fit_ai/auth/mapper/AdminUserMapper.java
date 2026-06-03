package org.raul.fit_ai.auth.mapper;

import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.model.AdminUser;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminUserMapper {

	private final PasswordEncoder passwordEncoder;

	public AdminUser toEntity(UUID adminId, RegisterRequestDTO request) {
		return AdminUser.builder()
				.firstName(request.firstName())
				.lastName(request.lastName())
				.email(request.email())
				.passwordHash(passwordEncoder.encode(request.password()))
				.createdBy(adminId)
				.build();
	}
}
