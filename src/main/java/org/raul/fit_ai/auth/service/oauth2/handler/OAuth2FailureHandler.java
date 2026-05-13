package org.raul.fit_ai.auth.service.oauth2.handler;

import org.raul.fit_ai.common.dto.BaseResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception
	) throws IOException {

		log.warn("OAuth2 authentication failed: {}", exception.getMessage());

		int status = resolveStatus(exception);
		String message = resolveMessage(exception);

		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		objectMapper.writeValue(response.getOutputStream(),
				BaseResponseDTO.error(message));
	}

	private int resolveStatus(AuthenticationException ex) {
		String msg = ex.getMessage();
		if (msg == null) return HttpServletResponse.SC_UNAUTHORIZED;

		return switch (msg) {
			case "Account is disabled" -> HttpServletResponse.SC_FORBIDDEN;
			case "Email not verified by OAuth2 provider",
			     "Email not provided by OAuth2 provider" -> HttpServletResponse.SC_UNPROCESSABLE_CONTENT;
			default -> HttpServletResponse.SC_UNAUTHORIZED;
		};
	}

	private String resolveMessage(AuthenticationException ex) {
		String msg = ex.getMessage();
		if (msg != null && msg.startsWith("Account already linked")) return msg;
		if (msg != null) return msg;
		return "OAuth2 authentication failed";
	}
}
