package org.raul.fit_ai.auth.service.oauth2;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo create(String registrationId, Map<String, Object> attributes) {

		if (registrationId.equalsIgnoreCase("google"))
			return new GoogleOAuth2UserInfo(attributes);

		throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
	}
}
