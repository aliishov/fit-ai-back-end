package org.raul.fit_ai.auth.annotation;

import org.raul.fit_ai.auth.util.ValidationPatterns;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = ValidationPatterns.PHONE,
		 message = "Invalid phone number format")
public @interface ValidPhone {
	String message() default "Invalid phone number";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
