package org.raul.fit_ai.common.exception;

public class DuplicateResourceException extends BaseException {
	public DuplicateResourceException(String message) {
		super(message);
	}

	public DuplicateResourceException(String message, Throwable cause) {
		super(message, cause);
	}
}
