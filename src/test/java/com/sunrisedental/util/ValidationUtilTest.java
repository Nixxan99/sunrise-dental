package com.sunrisedental.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Unit Tests for ValidationUtil (Task C Requirement).
 */
@DisplayName("ValidationUtil Tests")
class ValidationUtilTest {

    @Nested
    @DisplayName("Phone Number Validation (Sri Lankan Format e.g., 07[0-9]{8})")
    class PhoneNumberValidationTests {

        @ParameterizedTest(name = "Valid phone number: {0}")
        @ValueSource(strings = {
                "0712345678",
                "0771234567",
                "0701234567",
                "0729876543",
                "0755555555",
                "0761234567",
                "0781234567",
                "+94771234567",
                "0094771234567"
        })
        void shouldAcceptValidSriLankanPhoneNumbers(String phone) {
            assertTrue(ValidationUtil.isValidPhoneNumber(phone),
                    "Expected phone number to be valid: " + phone);
        }

        @ParameterizedTest(name = "Invalid phone number: {0}")
        @ValueSource(strings = {
                "071234567",      // 9 digits (too short)
                "07123456789",    // 11 digits (too long)
                "0112345678",     // Landline prefix 011, not 07
                "0612345678",     // Invalid prefix
                "abcdefghij",     // Non-numeric
                "077123456a",     // Mixed characters
                "",               // Empty string
                "          "      // Whitespace
        })
        void shouldRejectInvalidPhoneNumbers(String phone) {
            assertFalse(ValidationUtil.isValidPhoneNumber(phone),
                    "Expected phone number to be rejected: " + phone);
        }

        @Test
        void shouldRejectNullPhoneNumber() {
            assertFalse(ValidationUtil.isValidPhoneNumber(null),
                    "Null phone number should be rejected");
        }
    }

    @Nested
    @DisplayName("National Identity Card (NIC) Validation")
    class NicValidationTests {

        @ParameterizedTest(name = "Valid NIC: {0}")
        @ValueSource(strings = {
                "853451234V",
                "853451234v",
                "921234567X",
                "921234567x",
                "198534512340",
                "200012345678",
                " 853451234V "
        })
        void shouldAcceptValidSriLankanNics(String nic) {
            assertTrue(ValidationUtil.isValidNic(nic),
                    "Expected NIC to be valid: " + nic);
        }

        @ParameterizedTest(name = "Invalid NIC: {0}")
        @ValueSource(strings = {
                "85345123",        // 8 digits (too short)
                "8534512345V",     // 10 digits + V (too long)
                "853451234A",      // Invalid suffix letter
                "19853451234",     // 11 digits (new format must be 12)
                "1985345123401",   // 13 digits
                "abcdefghijk",     // Letters
                "",                // Empty
                "   "              // Whitespace
        })
        void shouldRejectInvalidNics(String nic) {
            assertFalse(ValidationUtil.isValidNic(nic),
                    "Expected NIC to be rejected: " + nic);
        }

        @Test
        void shouldRejectNullNic() {
            assertFalse(ValidationUtil.isValidNic(null),
                    "Null NIC should be rejected");
        }
    }

    @Nested
    @DisplayName("Appointment Date Validation (Not in the Past)")
    class AppointmentDateValidationTests {

        @Test
        void shouldAcceptTodayDate() {
            LocalDate today = LocalDate.now();
            assertTrue(ValidationUtil.isValidAppointmentDate(today),
                    "Today's date should be considered a valid appointment date");
        }

        @Test
        void shouldAcceptFutureDate() {
            LocalDate futureDate = LocalDate.now().plusDays(7);
            assertTrue(ValidationUtil.isValidAppointmentDate(futureDate),
                    "Future date should be a valid appointment date");
        }

        @Test
        void shouldRejectPastDate() {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            assertFalse(ValidationUtil.isValidAppointmentDate(yesterday),
                    "Past date should be rejected as an appointment date");
        }

        @Test
        void shouldRejectDateOneYearInThePast() {
            LocalDate pastDate = LocalDate.now().minusYears(1);
            assertFalse(ValidationUtil.isValidAppointmentDate(pastDate),
                    "Past year date should be rejected");
        }

        @Test
        void shouldRejectNullDate() {
            assertFalse(ValidationUtil.isValidAppointmentDate((LocalDate) null),
                    "Null date should be rejected");
        }

        @Test
        void shouldAcceptValidStringDateInFuture() {
            String futureDateStr = LocalDate.now().plusDays(5).toString();
            assertTrue(ValidationUtil.isValidAppointmentDate(futureDateStr),
                    "Valid ISO string date in future should be accepted");
        }

        @Test
        void shouldRejectPastStringDate() {
            String pastDateStr = LocalDate.now().minusDays(3).toString();
            assertFalse(ValidationUtil.isValidAppointmentDate(pastDateStr),
                    "Past string date should be rejected");
        }

        @Test
        void shouldRejectMalformedStringDate() {
            assertFalse(ValidationUtil.isValidAppointmentDate("invalid-date"));
            assertFalse(ValidationUtil.isValidAppointmentDate(""));
            assertFalse(ValidationUtil.isValidAppointmentDate((String) null));
        }
    }
}
