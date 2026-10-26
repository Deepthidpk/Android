package com.example.sqllite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NameValidatorTest {

    private static NameValidator.Error errorOf(String input) {
        return NameValidator.validate(input).error;
    }

    @Test
    public void nullAndBlankAreEmpty() {
        assertEquals(NameValidator.Error.EMPTY, errorOf(null));
        assertEquals(NameValidator.Error.EMPTY, errorOf(""));
        assertEquals(NameValidator.Error.EMPTY, errorOf("    "));
        assertEquals(NameValidator.Error.EMPTY, errorOf("\t\n"));
    }

    @Test
    public void singleCharacterIsTooShort() {
        assertEquals(NameValidator.Error.TOO_SHORT, errorOf("A"));
        assertEquals(NameValidator.Error.TOO_SHORT, errorOf("  B  "));
    }

    @Test
    public void tooLongIsRejected() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NameValidator.MAX_LENGTH + 1; i++) sb.append('a');
        assertEquals(NameValidator.Error.TOO_LONG, errorOf(sb.toString()));
    }

    @Test
    public void exactlyMaxLengthIsAccepted() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NameValidator.MAX_LENGTH; i++) sb.append('a');
        assertTrue(NameValidator.validate(sb.toString()).isValid());
    }

    @Test
    public void digitsAndSymbolsAreRejected() {
        assertEquals(NameValidator.Error.INVALID_CHARS, errorOf("John123"));
        assertEquals(NameValidator.Error.INVALID_CHARS, errorOf("12345"));
        assertEquals(NameValidator.Error.INVALID_CHARS, errorOf("Ann@home"));
        assertEquals(NameValidator.Error.INVALID_CHARS, errorOf("Robert'); DROP TABLE names;--"));
        assertEquals(NameValidator.Error.INVALID_CHARS, errorOf("-Ann"));
        assertEquals(NameValidator.Error.INVALID_CHARS, errorOf(".Ann"));
    }

    @Test
    public void realWorldNamesAreAccepted() {
        assertTrue(NameValidator.validate("Athira").isValid());
        assertTrue(NameValidator.validate("Mary-Jane").isValid());
        assertTrue(NameValidator.validate("O'Brien").isValid());
        assertTrue(NameValidator.validate("Dr. Smith").isValid());
        assertTrue(NameValidator.validate("José Álvarez").isValid());
        assertTrue(NameValidator.validate("अथिरा").isValid());   // Devanagari (combining vowel sign)
        assertTrue(NameValidator.validate("അതിര").isValid());     // Malayalam
    }

    @Test
    public void whitespaceIsTrimmedAndCollapsed() {
        NameValidator.Result r = NameValidator.validate("   Anna    Maria  ");
        assertTrue(r.isValid());
        assertEquals("Anna Maria", r.name);
    }

    @Test
    public void invalidResultIsNotValid() {
        assertFalse(NameValidator.validate("").isValid());
    }
}
