package org.raul.fit_ai.auth.service.oauth2.handler;

import org.raul.fit_ai.auth.dto.response.SignInResponseDTO;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.repository.AppUserRepository;
import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.raul.fit_ai.common.dto.BaseResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	JwtManager jwtManager;
	ObjectMapper objectMapper;
	AppUserRepository appUserRepository;

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException {

		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

		String accessToken  = jwtManager.generateAccessToken(principal);
		String refreshToken = jwtManager.generateRefreshToken(principal);

		log.info("OAuth2 login successful for user=[{}]", principal.getId());

		appUserRepository.updateLastSignInByUserId(principal.getId());

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		objectMapper.writeValue(response.getOutputStream(),
				BaseResponseDTO.success(
						new SignInResponseDTO(accessToken, refreshToken),
						"OAuth2 login successful"
				));
	}
}
