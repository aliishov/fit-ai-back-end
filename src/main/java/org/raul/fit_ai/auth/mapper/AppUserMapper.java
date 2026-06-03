package org.raul.fit_ai.auth.mapper;

import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.model.AppUser;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppUserMapper {

	private final PasswordEncoder passwordEncoder;

	public AppUser toEntity(RegisterRequestDTO request) {
		return AppUser.builder()
				.firstName(request.firstName())
				.lastName(request.lastName())
				.email(request.email())
				.passwordHash(passwordEncoder.encode(request.password()))
				.build();
	}
}
