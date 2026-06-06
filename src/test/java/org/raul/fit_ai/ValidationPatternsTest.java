package org.raul.fit_ai;

import org.raul.fit_ai.auth.util.ValidationPatterns;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ValidationPatternsTest {

    @Test
    void namePatternAcceptsTypicalNames() {
        assertTrue("John Doe".matches(ValidationPatterns.NAME));
        assertTrue("Анна-Мария".matches(ValidationPatterns.NAME));
        assertTrue("O'Neil".matches(ValidationPatterns.NAME));
    }

    @Test
    void namePatternRejectsDigitsAndSymbols() {
        assertFalse("John123".matches(ValidationPatterns.NAME));
        assertFalse("@@@".matches(ValidationPatterns.NAME));
    }

    @Test
    void phonePatternAcceptsE164() {
        assertTrue("+12345678901".matches(ValidationPatterns.PHONE));
        assertTrue("+994501234567".matches(ValidationPatterns.PHONE));
    }

    @Test
    void phonePatternRejectsInvalid() {
        assertFalse("123456".matches(ValidationPatterns.PHONE));
        assertFalse("+0123456".matches(ValidationPatterns.PHONE)); // leading zero after + invalid
    }

    @Test
    void emailPatternAccepts() {
        assertTrue("user@example.com".matches(ValidationPatterns.EMAIL));
        assertTrue("user.name+tag@sub.domain.co".matches(ValidationPatterns.EMAIL));
    }

    @Test
    void emailPatternRejects() {
        assertFalse("user@com".matches(ValidationPatterns.EMAIL));
        assertFalse("not-an-email".matches(ValidationPatterns.EMAIL));
    }

    @Test
    void otpPattern() {
        assertTrue("123456".matches(ValidationPatterns.OTP));
        assertFalse("12345".matches(ValidationPatterns.OTP));
        assertFalse("abcdef".matches(ValidationPatterns.OTP));
    }

    @Test
    void resetTokenPatternMatchesUuid() {
        String uuid = UUID.randomUUID().toString();
        assertTrue(uuid.matches(ValidationPatterns.RESET_TOKEN));
        assertFalse("not-a-uuid".matches(ValidationPatterns.RESET_TOKEN));
    }

    @Test
    void passwordCharacterClassPatterns() {
        assertTrue("Abc1@".matches(ValidationPatterns.PASSWORD_UPPERCASE));
        assertTrue("Abc1@".matches(ValidationPatterns.PASSWORD_LOWERCASE));
        assertTrue("Abc1@".matches(ValidationPatterns.PASSWORD_DIGIT));
        assertTrue("Abc1@".matches(ValidationPatterns.PASSWORD_SPECIAL));

        assertFalse("abc1@".matches(ValidationPatterns.PASSWORD_UPPERCASE));
        assertFalse("ABC1@".matches(ValidationPatterns.PASSWORD_LOWERCASE));
        assertFalse("Abc@".matches(ValidationPatterns.PASSWORD_DIGIT));
        assertFalse("Abc12".matches(ValidationPatterns.PASSWORD_SPECIAL));
    }
}
