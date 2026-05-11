package org.raul.fit_ai.auth.annotation;

import org.raul.fit_ai.auth.util.ValidationPatterns;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "Otp number is required")
@Pattern(regexp = ValidationPatterns.RESET_TOKEN,
		message = "Invalid reset token format")
public @interface ValidResetToken {
	String message() default "Invalid reset token";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
