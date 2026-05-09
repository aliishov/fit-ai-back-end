package org.raul.fit_ai.common.config;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.repository.AdminUserRepository;
import org.raul.fit_ai.auth.repository.AppUserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthProviderConfig {

	AdminUserRepository adminUserRepository;
	AppUserRepository appUserRepository;
	PasswordEncoder passwordEncoder;

	@Bean("adminUserDetailsService")
	public UserDetailsService adminUserDetailsService() {
		return identifier -> adminUserRepository.findByIdentifier(identifier)
				.map(admin -> new UserPrincipal(admin, identifier))
				.orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
	}

	@Bean("appUserDetailsService")
	public UserDetailsService appUserDetailsService() {
		return identifier -> appUserRepository.findByIdentifier(identifier)
				.map(appUser -> new UserPrincipal(appUser, identifier))
				.orElseThrow(() -> new UsernameNotFoundException("App user not found"));
	}

	@Bean("adminAuthenticationProvider")
	public AuthenticationProvider adminAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean("appAuthenticationProvider")
	public AuthenticationProvider appAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(appUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(
				adminAuthenticationProvider(),
				appAuthenticationProvider()
		);
	}
}
