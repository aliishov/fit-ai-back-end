package org.raul.fit_ai.auth.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.raul.fit_ai.auth.util.ValidationPatterns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = ValidationPatterns.EMAIL,
		message = "Invalid email format")
@Size(
		max = 320,
		message = "Email must not exceed 320 characters"
)
@NotBlank(message = "Email is required")
public @interface ValidEmail {
	String message() default "Invalid email";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
