package org.raul.fit_ai.common.exception;

public class PasswordsDoNotMatchException extends BaseException {
	public PasswordsDoNotMatchException(String message) {
		super(message);
	}

	public PasswordsDoNotMatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
