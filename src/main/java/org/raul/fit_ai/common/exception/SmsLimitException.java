package org.raul.fit_ai.common.exception;

public class SmsLimitException extends BaseException {
	public SmsLimitException(String message) {
		super(message);
	}

	public SmsLimitException(String message, Throwable cause) {
		super(message, cause);
	}
}
