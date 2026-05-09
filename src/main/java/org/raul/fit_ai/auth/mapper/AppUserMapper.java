package org.raul.fit_ai.auth.mapper;

import org.raul.fit_ai.auth.dto.request.RegisterRequestDTO;
import org.raul.fit_ai.auth.model.AppUser;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserMapper {

	PasswordEncoder passwordEncoder;

	public AppUser toEntity(RegisterRequestDTO request) {
		return AppUser.builder()
				.firstName(request.firstName())
				.lastName(request.lastName())
				.email(request.email())
				.passwordHash(passwordEncoder.encode(request.password()))
				.build();
	}
}
