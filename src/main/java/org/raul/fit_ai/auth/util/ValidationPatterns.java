package org.raul.fit_ai.auth.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationPatterns {
	public static final String NAME = "^[\\p{L}\\p{M}\\s'-]+$";
	public static final String PHONE = "^\\+[1-9]\\d{6,14}$";
	public static final String EMAIL = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
	public static final String OTP = "^[0-9]{6}$";
	public static final String RESET_TOKEN = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
	public static final String PASSWORD_UPPERCASE = ".*[A-Z].*";
	public static final String PASSWORD_LOWERCASE = ".*[a-z].*";
	public static final String PASSWORD_DIGIT = ".*\\d.*";
	public static final String PASSWORD_SPECIAL = ".*[@$!%*?&\\-_.+#^()|~].*";
}
