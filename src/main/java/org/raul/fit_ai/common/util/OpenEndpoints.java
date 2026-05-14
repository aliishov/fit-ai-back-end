package org.raul.fit_ai.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenEndpoints {
	public static final String[] OPEN_ENDPOINTS = {
			"/api/v1/app/auth/users",
			"/api/v1/app/auth/sessions",
			"/api/v1/app/auth/passwords/reset-request",
			"/api/v1/app/auth/passwords/reset",

			"/api/v1/admin/auth/sessions",
			"/api/v1/admin/auth/passwords/reset-request",
			"/api/v1/admin/auth/passwords/reset",

			"/api/v1/auth/otp/verify",

			"/oauth2/**",
			"/login/oauth2/**",
			"/actuator/**",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/swagger-resources",
			"/swagger-resources/**",
			"/swagger-ui.html",
			"/swagger-ui/index.html",
			"/error",
			"/error/**",
			"/webjars/**",
			"/favicon.ico"
	};
}
