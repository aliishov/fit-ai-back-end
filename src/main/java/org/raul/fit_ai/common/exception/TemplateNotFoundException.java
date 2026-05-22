package org.raul.fit_ai.common.exception;

public class TemplateNotFoundException extends BaseException {
	public TemplateNotFoundException(String message) {
		super(message);
	}

	public TemplateNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
