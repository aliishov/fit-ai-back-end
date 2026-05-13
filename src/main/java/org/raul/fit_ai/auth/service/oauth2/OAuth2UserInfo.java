package org.raul.fit_ai.auth.service.oauth2;

import java.util.Map;

public abstract class OAuth2UserInfo {

	protected final Map<String, Object> attributes;

	protected OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract String getProviderId();
	public abstract String getEmail();
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getAvatarUrl();
	public abstract boolean isEmailVerified();
}
