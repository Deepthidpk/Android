package com.example.sqllite;

import java.util.regex.Pattern;

/**
 * Pure-Java input validation for the name field.
 * Has no Android dependencies, so it can be unit tested on the JVM.
 */
public final class NameValidator {

    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 50;

    /** Why a name was rejected. */
    public enum Error { NONE, EMPTY, TOO_SHORT, TOO_LONG, INVALID_CHARS }

    /**
     * Letters first, then letters / combining marks / space / . ' - (any language).
     * \p{M} is needed for scripts such as Malayalam and Devanagari, where vowel signs
     * are combining marks rather than letters.
     */
    private static final Pattern VALID = Pattern.compile("^\\p{L}[\\p{L}\\p{M} .'\\-]*$");

    private NameValidator() { }

    public static final class Result {
        public final Error error;
        /** Trimmed name with repeated spaces collapsed; only meaningful when valid. */
        public final String name;

        Result(Error error, String name) {
            this.error = error;
            this.name = name;
        }

        public boolean isValid() {
            return error == Error.NONE;
        }
    }

    public static Result validate(String input) {
        String name = normalize(input);

        if (name.isEmpty()) {
            return new Result(Error.EMPTY, name);
        }
        if (name.length() < MIN_LENGTH) {
            return new Result(Error.TOO_SHORT, name);
        }
        if (name.length() > MAX_LENGTH) {
            return new Result(Error.TOO_LONG, name);
        }
        if (!VALID.matcher(name).matches()) {
            return new Result(Error.INVALID_CHARS, name);
        }
        return new Result(Error.NONE, name);
    }

    /** Trims and collapses runs of whitespace into a single space. */
    static String normalize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }
}
