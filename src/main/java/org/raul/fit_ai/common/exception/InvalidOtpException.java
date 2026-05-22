package org.raul.fit_ai.common.exception;

public class InvalidOtpException extends BaseException {
	public InvalidOtpException(String message) {
		super(message);
	}

	public InvalidOtpException(String message, Throwable cause) {
		super(message, cause);
	}
}
