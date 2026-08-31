package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for NotificationService verifying Observer & Strategy patterns (CIS6003 Task B).
 */
@DisplayName("NotificationService Pattern and Alert Generation Tests")
class NotificationServiceTest {

    private NotificationService notificationService;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();

        testAppointment = new Appointment();
        testAppointment.setAppointmentNumber(101);
        testAppointment.setPatientId(5);
        testAppointment.setPatientName("Sunil Perera");
        testAppointment.setDentistId(2);
        testAppointment.setDentistName("Dr. Kasun Silva");
        testAppointment.setTreatmentId(3);
        testAppointment.setTreatmentName("Root Canal Therapy");
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(2));
        testAppointment.setAppointmentTime(LocalTime.of(10, 30));
        testAppointment.setCost(8500.00);
        testAppointment.setStatus("SCHEDULED");
    }

    @Nested
    @DisplayName("Strategy Pattern SMS Formatting Tests")
    class SmsStrategyTests {

        @Test
        @DisplayName("Should format SMS alert with patient name, doctor, date, and procedure")
        void shouldFormatSmsAlertWithAllDetails() {
            String sms = notificationService.formatSmsNotification(testAppointment);

            assertNotNull(sms);
            assertTrue(sms.contains("Sunil Perera"), "SMS must contain the patient name");
            assertTrue(sms.contains("#101"), "SMS must contain the appointment number");
            assertTrue(sms.contains("Dr. Kasun Silva"), "SMS must contain the dentist name");
            assertTrue(sms.contains("Root Canal Therapy"), "SMS must contain the treatment procedure name");
            assertTrue(sms.contains("Sunrise Dental Clinic"), "SMS must contain clinic branding");
        }

        @Test
        @DisplayName("Should format SMS gracefully when presentation fields are empty")
        void shouldHandleFallbackFieldsInSms() {
            Appointment sparseAppt = new Appointment();
            sparseAppt.setAppointmentNumber(202);
            sparseAppt.setAppointmentDate(LocalDate.now().plusDays(1));
            sparseAppt.setAppointmentTime(LocalTime.of(14, 0));

            String sms = notificationService.formatSmsNotification(sparseAppt);

            assertNotNull(sms);
            assertTrue(sms.contains("#202"));
            assertTrue(sms.contains("Valued Patient"));
            assertTrue(sms.contains("Assigned Doctor"));
            assertTrue(sms.contains("Dental Treatment"));
        }
    }

    @Nested
    @DisplayName("Observer Pattern Dispatch Tests")
    class ObserverPatternTests {

        @Test
        @DisplayName("Should notify all registered observers on appointment booked")
        void shouldNotifyObservers() {
            String contactNumber = "0771234567";
            List<NotificationService.NotificationResult> results =
                    notificationService.notifyAppointmentBooked(testAppointment, contactNumber);

            assertNotNull(results);
            assertEquals(2, results.size(), "Default service should notify SMS and Email observers");

            // Verify SMS result
            NotificationService.NotificationResult smsResult = results.stream()
                    .filter(r -> "SMS".equals(r.getChannel()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(smsResult);
            assertTrue(smsResult.isDelivered());
            assertEquals(contactNumber, smsResult.getRecipient());
            assertTrue(smsResult.getMessage().contains("Sunil Perera"));

            // Verify Email result
            NotificationService.NotificationResult emailResult = results.stream()
                    .filter(r -> "EMAIL".equals(r.getChannel()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(emailResult);
            assertTrue(emailResult.isDelivered());
            assertTrue(emailResult.getMessage().contains("Root Canal Therapy"));
        }

        @Test
        @DisplayName("Should allow adding and removing custom observers")
        void shouldAllowAddingAndRemovingObservers() {
            final boolean[] customObserverFired = {false};

            NotificationService.AppointmentObserver customObserver = (appt, contact) -> {
                customObserverFired[0] = true;
                return new NotificationService.NotificationResult("CUSTOM_PUSH", contact, "Custom push alert", true);
            };

            notificationService.registerObserver(customObserver);
            List<NotificationService.NotificationResult> results =
                    notificationService.notifyAppointmentBooked(testAppointment, "0719876543");

            assertEquals(3, results.size());
            assertTrue(customObserverFired[0]);

            // Remove observer and verify
            notificationService.removeObserver(customObserver);
            customObserverFired[0] = false;
            results = notificationService.notifyAppointmentBooked(testAppointment, "0719876543");
            assertEquals(2, results.size());
            assertFalse(customObserverFired[0]);
        }

        @Test
        @DisplayName("Should return empty list if appointment is null")
        void shouldReturnEmptyIfNullAppointment() {
            List<NotificationService.NotificationResult> results =
                    notificationService.notifyAppointmentBooked(null, "0771234567");
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }
    }
}
