package org.raul.fit_ai.auth.service.oauth2;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{

	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getProviderId() {
		return (String) attributes.get("sub");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getFirstName() {
		return (String) attributes.get("given_name");
	}

	@Override
	public String getLastName() {
		return (String) attributes.get("family_name");
	}

	@Override
	public String getAvatarUrl() {
		return (String) attributes.get("picture");
	}

	@Override
	public boolean isEmailVerified() {
		Object verified = attributes.get("email_verified");
		if (verified instanceof Boolean b) return b;
		if (verified instanceof String s) return Boolean.parseBoolean(s);
		return false;
	}
}
