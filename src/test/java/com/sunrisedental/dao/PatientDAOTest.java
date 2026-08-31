package com.sunrisedental.dao;

import com.sunrisedental.dao.impl.PatientDAOImpl;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.PatientAppointmentHistoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit & Integration tests for PatientDAO operations, patient clinical history, and retention analytics.
 */
@DisplayName("PatientDAO Management, History Tracking, and Analytics Tests")
class PatientDAOTest {

    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() {
        patientDAO = new PatientDAOImpl();
    }

    @Test
    @DisplayName("Should retrieve all registered patients")
    void shouldGetAllPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        assertNotNull(patients, "Patients list should not be null");
        assertFalse(patients.isEmpty(), "Should return registered patients in database");

        Patient first = patients.get(0);
        assertTrue(first.getPatientId() > 0);
        assertNotNull(first.getFullName());
        assertNotNull(first.getContactNumber());
    }

    @Test
    @DisplayName("Should retrieve patient by valid ID")
    void shouldGetPatientById() {
        List<Patient> all = patientDAO.getAllPatients();
        assertFalse(all.isEmpty());

        int targetId = all.get(0).getPatientId();
        Patient patient = patientDAO.getPatientById(targetId);

        assertNotNull(patient);
        assertEquals(targetId, patient.getPatientId());
        assertEquals(all.get(0).getFullName(), patient.getFullName());
    }

    @Test
    @DisplayName("Should return null for non-existent patient ID")
    void shouldReturnNullForMissingPatient() {
        Patient patient = patientDAO.getPatientById(999999);
        assertNull(patient);
    }

    @Test
    @DisplayName("Should register a new patient and return generated ID, then update and retrieve")
    void shouldRegisterAndUpdatePatient() {
        String testName = "Test Patient " + System.currentTimeMillis();
        String testContact = "077" + (int)(Math.random() * 9000000 + 1000000);
        String testAddress = "123 Sample Avenue, Colombo";

        Patient newPatient = new Patient(testName, testAddress, testContact);
        int generatedId = patientDAO.registerPatient(newPatient);

        assertTrue(generatedId > 0, "Generated patient ID should be positive");
        assertEquals(generatedId, newPatient.getPatientId());

        // Verify retrieval
        Patient fetched = patientDAO.getPatientById(generatedId);
        assertNotNull(fetched);
        assertEquals(testName, fetched.getFullName());
        assertEquals(testContact, fetched.getContactNumber());
        assertEquals(testAddress, fetched.getAddress());

        // Update record
        fetched.setFullName(testName + " Updated");
        fetched.setAddress("456 Renovated Lane, Kandy");
        fetched.setContactNumber("0719876543");

        boolean updated = patientDAO.updatePatient(fetched);
        assertTrue(updated, "Patient record update should succeed");

        Patient updatedRecord = patientDAO.getPatientById(generatedId);
        assertNotNull(updatedRecord);
        assertEquals(testName + " Updated", updatedRecord.getFullName());
        assertEquals("456 Renovated Lane, Kandy", updatedRecord.getAddress());
        assertEquals("0719876543", updatedRecord.getContactNumber());
    }

    @Test
    @DisplayName("Should search patients by name or contact number")
    void shouldSearchPatientsByNameOrContact() {
        List<Patient> all = patientDAO.getAllPatients();
        assertFalse(all.isEmpty());

        Patient sample = all.get(0);
        String nameSub = sample.getFullName().substring(0, Math.min(4, sample.getFullName().length()));

        List<Patient> searchResultsName = patientDAO.searchPatients(nameSub);
        assertNotNull(searchResultsName);
        assertFalse(searchResultsName.isEmpty(), "Should find patients matching name snippet: " + nameSub);

        List<Patient> searchResultsContact = patientDAO.searchPatients(sample.getContactNumber());
        assertNotNull(searchResultsContact);
        assertFalse(searchResultsContact.isEmpty(), "Should find patient matching contact number");

        // Empty search returns all
        List<Patient> emptySearch = patientDAO.searchPatients("");
        assertNotNull(emptySearch);
        assertEquals(all.size(), emptySearch.size());
    }

    @Test
    @DisplayName("Should retrieve patient appointment clinical history")
    void shouldGetPatientAppointmentHistory() {
        List<Patient> all = patientDAO.getAllPatients();
        assertFalse(all.isEmpty());

        int sampleId = all.get(0).getPatientId();
        List<PatientAppointmentHistoryItem> history = patientDAO.getPatientAppointmentHistory(sampleId);
        assertNotNull(history, "History list should not be null even if empty");

        if (!history.isEmpty()) {
            PatientAppointmentHistoryItem item = history.get(0);
            assertTrue(item.getAppointmentNumber() > 0);
            assertNotNull(item.getDentistName());
            assertNotNull(item.getTreatmentName());
            assertNotNull(item.getAppointmentDate());
            assertNotNull(item.getPaymentStatus());
        }
    }

    @Test
    @DisplayName("Should retrieve patient retention analytics metrics")
    void shouldGetPatientAnalytics() {
        Map<String, Object> analytics = patientDAO.getPatientAnalytics();
        assertNotNull(analytics, "Analytics map should not be null");

        assertTrue(analytics.containsKey("totalUniquePatients"));
        assertTrue(analytics.containsKey("repeatPatients"));
        assertTrue(analytics.containsKey("singleVisitPatients"));
        assertTrue(analytics.containsKey("newPatientsThisMonth"));
        assertTrue(analytics.containsKey("repeatRate"));
        assertTrue(analytics.containsKey("retentionRatio"));

        int total = (Integer) analytics.get("totalUniquePatients");
        int repeat = (Integer) analytics.get("repeatPatients");
        double repeatRate = (Double) analytics.get("repeatRate");

        assertTrue(total >= 0);
        assertTrue(repeat >= 0);
        assertTrue(repeat <= total);
        assertTrue(repeatRate >= 0.0 && repeatRate <= 100.0);
    }

    @Test
    @DisplayName("Should safely handle null inputs in register and update")
    void shouldHandleNullInputsSafely() {
        assertEquals(-1, patientDAO.registerPatient(null));
        assertFalse(patientDAO.updatePatient(null));
        assertFalse(patientDAO.updatePatient(new Patient(0, "Invalid", "None", "0000")));
    }
}
