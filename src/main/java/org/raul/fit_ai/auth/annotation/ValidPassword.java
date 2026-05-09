package org.raul.fit_ai.auth.annotation;

import org.raul.fit_ai.auth.util.ValidationPatterns;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "Password is required")
@Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
@Pattern.List({
		@Pattern(
				regexp = ValidationPatterns.PASSWORD_UPPERCASE,
				message = "Password must contain at least one uppercase letter"
		),
		@Pattern(
				regexp = ValidationPatterns.PASSWORD_LOWERCASE,
				message = "Password must contain at least one lowercase letter"
		),
		@Pattern(
				regexp = ValidationPatterns.PASSWORD_DIGIT,
				message = "Password must contain at least one digit"
		),
		@Pattern(
				regexp = ValidationPatterns.PASSWORD_SPECIAL,
				message = "Password must contain at least one special character"
		)
})
public @interface ValidPassword {
	String message() default "Invalid password";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
