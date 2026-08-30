package com.sunrisedental.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Validation Utility providing input validation rules for Sunrise Dental Clinic System.
 */
public final class ValidationUtil {

    /**
     * Sri Lankan mobile number pattern:
     * Accepts:
     * - Standard national format: 07[0-9]{8} (e.g. 0712345678)
     * - International format: +947[0-9]{8} (e.g. +94771234567)
     * - Dialing code format: 00947[0-9]{8} (e.g. 0094771234567)
     */
    private static final Pattern SRI_LANKAN_PHONE_PATTERN =
            Pattern.compile("^(?:07\\d{8}|\\+947\\d{8}|00947\\d{8})$");

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Validates if a phone number complies with the Sri Lankan mobile format.
     *
     * @param phoneNumber string phone number to validate
     * @return true if valid, false if null, empty, or non-matching
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        String cleanNumber = phoneNumber.trim().replaceAll("[\\s-]", "");
        return SRI_LANKAN_PHONE_PATTERN.matcher(cleanNumber).matches();
    }

    /**
     * Alias for isValidPhoneNumber.
     */
    public static boolean isValidSriLankanPhone(String phoneNumber) {
        return isValidPhoneNumber(phoneNumber);
    }

    /**
     * Validates that an appointment date is not in the past (today or future is valid).
     *
     * @param appointmentDate the appointment date
     * @return true if non-null and on/after current date; false otherwise
     */
    public static boolean isValidAppointmentDate(LocalDate appointmentDate) {
        if (appointmentDate == null) {
            return false;
        }
        return !appointmentDate.isBefore(LocalDate.now());
    }

    /**
     * Validates that an appointment date in ISO-8601 String format (YYYY-MM-DD) is not in the past.
     *
     * @param dateString ISO date string
     * @return true if valid format and not in past; false otherwise
     */
    public static boolean isValidAppointmentDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(dateString.trim());
            return isValidAppointmentDate(date);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks if a monetary amount is non-negative (>= 0).
     *
     * @param amount the fee or monetary value
     * @return true if amount >= 0, false if negative or NaN
     */
    public static boolean isNonNegative(double amount) {
        return !Double.isNaN(amount) && amount >= 0.0;
    }

    /**
     * Checks if a string is not null and not empty after trimming.
     *
     * @param value string to check
     * @return true if non-empty, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
