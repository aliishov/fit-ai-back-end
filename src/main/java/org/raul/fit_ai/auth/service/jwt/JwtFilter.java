package org.raul.fit_ai.auth.service.jwt;

import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.model.enumerated.Role;
import org.raul.fit_ai.common.dto.BaseResponseDTO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	JwtManager jwtManager;
	UserDetailsService appUserDetailsService;
	UserDetailsService adminUserDetailsService;
	ObjectMapper objectMapper;

	public JwtFilter(
			JwtManager jwtManager,
			@Qualifier("appUserDetailsService") UserDetailsService appUserDetailsService,
			@Qualifier("adminUserDetailsService") UserDetailsService adminUserDetailsService,
			ObjectMapper objectMapper) {
		this.jwtManager = jwtManager;
		this.appUserDetailsService = appUserDetailsService;
		this.adminUserDetailsService = adminUserDetailsService;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwtToken = authHeader.substring(7);
		final String identifier;

		try {
			identifier = jwtManager.extractUsername(jwtToken);
		} catch (JwtException e) {
			log.warn("Invalid JWT token for [{} {}]: {}",
					request.getMethod(), request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, e);
			return;
		}

		if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			String userType = jwtManager.extractUserType(jwtToken);

			UserDetailsService targetService = Role.ROLE_ADMIN.name().equals(userType)
					? adminUserDetailsService
					: appUserDetailsService;

			try {
				UserDetails userDetails = targetService.loadUserByUsername(identifier);
				UserPrincipal principal = (UserPrincipal) userDetails;

				if (jwtManager.isTokenValid(jwtToken, principal)) {
					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(
									principal, null, principal.getAuthorities());

					authentication.setDetails(
							new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);
					log.debug("Authenticated identifier=[{}] type=[{}]", identifier, userType);
				}
			} catch (UsernameNotFoundException ex) {
				log.warn("User not found identifier=[{}] userType=[{}]", identifier, userType);
			} catch (ClassCastException ex) {
				log.error("UserDetails is not UserPrincipal for identifier=[{}]", identifier);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void writeErrorResponse(HttpServletResponse response, JwtException e) throws IOException {
		String message = e instanceof ExpiredJwtException
				? "JWT token has expired"
				: "JWT token is invalid or malformed";

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getOutputStream(),
				BaseResponseDTO.error(message));
	}
}
