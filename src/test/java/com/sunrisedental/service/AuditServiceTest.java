package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentAuditDAO;
import com.sunrisedental.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying AuditService and the application-tier Observer pattern
 * replacing database triggers for distributed TiDB compatibility.
 */
@DisplayName("AuditService Observer Pattern and Application-Tier Auditing Tests")
class AuditServiceTest {

    private Appointment testAppointment;
    private List<String> capturedAudits;
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        testAppointment = new Appointment();
        testAppointment.setAppointmentNumber(501);
        testAppointment.setPatientId(10);
        testAppointment.setDentistId(3);
        testAppointment.setTreatmentId(2);
        testAppointment.setAppointmentDate(LocalDate.of(2026, 9, 15));
        testAppointment.setAppointmentTime(LocalTime.of(14, 30));
        testAppointment.setStatus("SCHEDULED");

        capturedAudits = new ArrayList<>();
        AppointmentAuditDAO mockAuditDAO = (appointmentNumber, actionType, performedBy, details) -> {
            capturedAudits.add(actionType + ":" + appointmentNumber + ":" + performedBy + ":" + details);
            return true;
        };

        auditService = new AuditService(mockAuditDAO);
    }

    @Test
    @DisplayName("Should successfully log audit record on appointment registration callback")
    void shouldLogAuditOnAppointmentRegistered() {
        auditService.onAppointmentRegistered(testAppointment, "admin_user");

        assertEquals(1, capturedAudits.size());
        String logEntry = capturedAudits.get(0);
        assertTrue(logEntry.startsWith("APPOINTMENT_REGISTERED:501:admin_user:"));
        assertTrue(logEntry.contains("Patient ID: 10"));
        assertTrue(logEntry.contains("Dentist ID: 3"));
        assertTrue(logEntry.contains("2026-09-15"));
    }

    @Test
    @DisplayName("Should default to SYSTEM performedBy if null or empty")
    void shouldDefaultPerformedByToSystem() {
        auditService.onAppointmentRegistered(testAppointment, null);

        assertEquals(1, capturedAudits.size());
        String logEntry = capturedAudits.get(0);
        assertTrue(logEntry.startsWith("APPOINTMENT_REGISTERED:501:SYSTEM:"));
    }

    @Test
    @DisplayName("Should handle null appointment safely without throwing exceptions")
    void shouldHandleNullAppointmentSafely() {
        auditService.onAppointmentRegistered(null, "system");
        assertTrue(capturedAudits.isEmpty());
    }
}
