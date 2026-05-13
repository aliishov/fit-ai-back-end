package org.raul.fit_ai.common.config;

import org.raul.fit_ai.auth.service.SignOutHandler;
import org.raul.fit_ai.auth.service.jwt.JwtFilter;
import org.raul.fit_ai.auth.service.oauth2.CustomOAuth2UserService;
import org.raul.fit_ai.auth.service.oauth2.handler.OAuth2FailureHandler;
import org.raul.fit_ai.auth.service.oauth2.handler.OAuth2SuccessHandler;
import org.raul.fit_ai.auth.util.JwtProperties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.raul.fit_ai.common.util.OpenEndpoints.OPEN_ENDPOINTS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

	JwtFilter jwtFilter;
	SignOutHandler signOutHandler;
	CustomOAuth2UserService customOAuth2UserService;
	OAuth2SuccessHandler oAuth2SuccessHandler;
	OAuth2FailureHandler oAuth2FailureHandler;

	@Bean
	@Order(1)
	public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/oauth2/**", "/login/oauth2/**")
				.csrf(AbstractHttpConfigurer::disable)
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService)
						)
						.successHandler(oAuth2SuccessHandler)
						.failureHandler(oAuth2FailureHandler)
				)
				.build();
	}
	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		return http
				.cors(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(request -> request
						.requestMatchers(OPEN_ENDPOINTS).permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout
						.logoutUrl("/api/v1/auth/logout")
						.addLogoutHandler(signOutHandler)
						.logoutSuccessHandler((request, response, authentication) ->
								SecurityContextHolder.clearContext())
				)
				.build();
	}
}
