package org.raul.fit_ai.auth.util;

import org.raul.fit_ai.auth.annotation.ValidEmailOrPhone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailOrPhoneValidator implements ConstraintValidator<ValidEmailOrPhone, String> {

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
			ValidationPatterns.EMAIL
	);

	private static final Pattern PHONE_PATTERN = Pattern.compile(
			ValidationPatterns.PHONE
	);

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return true;
		}

		boolean isValidEmail = EMAIL_PATTERN.matcher(value).matches();
		boolean isValidPhone = PHONE_PATTERN.matcher(value).matches();

		if (isValidEmail || isValidPhone) {
			return true;
		}

		context.disableDefaultConstraintViolation();

		if (value.contains("@")) {
			context.buildConstraintViolationWithTemplate(
					"Invalid email address format"
			).addConstraintViolation();
		} else {
			context.buildConstraintViolationWithTemplate(
					"Phone number must be in E.164 format (e.g. +994501234567)"
			).addConstraintViolation();
		}

		return false;
	}
}
