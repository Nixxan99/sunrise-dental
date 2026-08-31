package com.sunrisedental.service;

import com.sunrisedental.dao.NotificationLogDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.PatientAppointmentHistoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for NotificationService verifying Strategy pattern implementations (CIS6003 Task B).
 */
@DisplayName("NotificationService Strategy Pattern and Alert Generation Tests")
class NotificationServiceTest {

    private Appointment testAppointment;
    private NotificationLogDAO mockLogDAO;
    private PatientDAO mockPatientDAO;

    @BeforeEach
    void setUp() {
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

        // Stub/Mock DAOs without database dependency
        mockLogDAO = (recipientPhone, messageBody, status) -> true;
        mockPatientDAO = new PatientDAO() {
            @Override
            public boolean createPatient(Patient patient) { return true; }
            @Override
            public int registerPatient(Patient patient) { return 1; }
            @Override
            public boolean updatePatient(Patient patient) { return true; }
            @Override
            public Patient getPatientById(int patientId) {
                Patient p = new Patient();
                p.setPatientId(patientId);
                p.setFullName("Sunil Perera");
                p.setContactNumber("0771234567");
                return p;
            }
            @Override
            public Patient getPatientByContactNumber(String contactNumber) { return null; }
            @Override
            public List<Patient> getAllPatients() { return Collections.emptyList(); }
            @Override
            public List<Patient> searchPatients(String query) { return Collections.emptyList(); }
            @Override
            public List<PatientAppointmentHistoryItem> getPatientAppointmentHistory(int patientId) { return Collections.emptyList(); }
            @Override
            public Map<String, Object> getPatientAnalytics() { return Collections.emptyMap(); }
        };
    }

    @Nested
    @DisplayName("SmsNotificationService Strategy Tests")
    class SmsNotificationServiceTests {

        @Test
        @DisplayName("Should format SMS alert with complete clinical details")
        void shouldFormatSmsAlertWithAllDetails() {
            NotificationService smsService = new SmsNotificationService(mockLogDAO, mockPatientDAO);
            String sms = smsService.formatAppointmentAlert(testAppointment);

            assertNotNull(sms);
            assertTrue(sms.contains("Sunil Perera"), "SMS must contain the patient name");
            assertTrue(sms.contains("#101"), "SMS must contain the appointment number");
            assertTrue(sms.contains("Dr. Kasun Silva"), "SMS must contain the dentist name");
            assertTrue(sms.contains("Root Canal Therapy"), "SMS must contain the treatment procedure name");
            assertTrue(sms.contains("Sunrise Dental Clinic"), "SMS must contain clinic branding");
        }

        @Test
        @DisplayName("Should format SMS gracefully when presentation fields are sparse")
        void shouldHandleFallbackFieldsInSms() {
            Appointment sparseAppt = new Appointment();
            sparseAppt.setAppointmentNumber(202);
            sparseAppt.setAppointmentDate(LocalDate.now().plusDays(1));
            sparseAppt.setAppointmentTime(LocalTime.of(14, 0));

            NotificationService smsService = new SmsNotificationService(mockLogDAO, mockPatientDAO);
            String sms = smsService.formatAppointmentAlert(sparseAppt);

            assertNotNull(sms);
            assertTrue(sms.contains("#202"));
            assertTrue(sms.contains("Valued Patient"));
            assertTrue(sms.contains("Assigned Doctor"));
            assertTrue(sms.contains("Dental Treatment"));
        }

        @Test
        @DisplayName("Should send appointment alert and return true")
        void shouldSendAppointmentAlertSuccessfully() {
            NotificationService smsService = new SmsNotificationService(mockLogDAO, mockPatientDAO);
            boolean result = smsService.sendAppointmentAlert(testAppointment);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when appointment is null")
        void shouldReturnFalseForNullAppointment() {
            NotificationService smsService = new SmsNotificationService(mockLogDAO, mockPatientDAO);
            assertFalse(smsService.sendAppointmentAlert(null));
        }
    }

    @Nested
    @DisplayName("GmailNotificationService Strategy Tests")
    class GmailNotificationServiceTests {

        @Test
        @DisplayName("Should format email alert with subject and clinical schedule details")
        void shouldFormatEmailAlertWithFullDetails() {
            NotificationService gmailService = new GmailNotificationService(mockLogDAO, mockPatientDAO);
            String email = gmailService.formatAppointmentAlert(testAppointment);

            assertNotNull(email);
            assertTrue(email.contains("Subject: Appointment Confirmation"));
            assertTrue(email.contains("Sunil Perera"));
            assertTrue(email.contains("Root Canal Therapy"));
            assertTrue(email.contains("Dr. Kasun Silva"));
            assertTrue(email.contains("Sunrise Dental Clinic"));
        }

        @Test
        @DisplayName("Should dispatch email alert and return true")
        void shouldSendEmailAlertSuccessfully() {
            NotificationService gmailService = new GmailNotificationService(mockLogDAO, mockPatientDAO);
            boolean result = gmailService.sendAppointmentAlert(testAppointment);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when appointment is null")
        void shouldReturnFalseForNullAppointmentInGmail() {
            NotificationService gmailService = new GmailNotificationService(mockLogDAO, mockPatientDAO);
            assertFalse(gmailService.sendAppointmentAlert(null));
        }
    }
}
