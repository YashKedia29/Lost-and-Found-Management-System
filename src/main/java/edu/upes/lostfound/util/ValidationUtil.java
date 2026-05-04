package edu.upes.lostfound.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");

    private ValidationUtil() {
    }

    public static void requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    public static void validateEmail(String email) {
        requireText(email, "Email");
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
    }

    public static void validatePhone(String phone) {
        requireText(phone, "Phone");
        if (!PHONE.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Phone must be exactly 10 digits.");
        }
    }

    public static LocalDate parseDate(String value, String label) {
        requireText(value, label);
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(label + " must be in yyyy-mm-dd format.");
        }
    }
}
