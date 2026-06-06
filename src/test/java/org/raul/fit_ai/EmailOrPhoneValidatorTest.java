package org.raul.fit_ai;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.raul.fit_ai.auth.util.EmailOrPhoneValidator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailOrPhoneValidatorTest {

    private EmailOrPhoneValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new EmailOrPhoneValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void nullAndBlankAreValid() {
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
        verifyNoInteractions(context);
    }

    @Test
    void validEmailIsAccepted() {
        assertTrue(validator.isValid("user@example.com", context));
        verifyNoInteractions(context);
    }

    @Test
    void validPhoneIsAccepted() {
        assertTrue(validator.isValid("+12345678901", context));
        verifyNoInteractions(context);
    }

    @Test
    void invalidEmailProducesEmailMessage() {
        String badEmail = "bad@com"; // missing proper domain TLD

        boolean result = validator.isValid(badEmail, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Invalid email address format");
        verify(builder).addConstraintViolation();
    }

    @Test
    void invalidPhoneProducesPhoneMessage() {
        String badPhone = "12345"; // no + and too short

        boolean result = validator.isValid(badPhone, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Phone number must be in E.164 format (e.g. +994501234567)");
        verify(builder).addConstraintViolation();
    }
}
