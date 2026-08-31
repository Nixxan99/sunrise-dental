package com.sunrisedental.dao;

import com.sunrisedental.dao.impl.TreatmentDAOImpl;
import com.sunrisedental.model.Treatment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit & Integration tests for TreatmentDAO CRUD operations.
 */
@DisplayName("TreatmentDAO CRUD Operations and Model Tests")
class TreatmentDAOTest {

    private TreatmentDAO treatmentDAO;

    @BeforeEach
    void setUp() {
        treatmentDAO = new TreatmentDAOImpl();
    }

    @Test
    @DisplayName("Should retrieve all treatments from catalog")
    void shouldGetAllTreatments() {
        List<Treatment> treatments = treatmentDAO.getAllTreatments();
        assertNotNull(treatments, "Treatments list should not be null");
        assertFalse(treatments.isEmpty(), "Catalog should contain seed treatments");

        Treatment first = treatments.get(0);
        assertTrue(first.getTreatmentId() > 0);
        assertNotNull(first.getTreatmentName());
        assertTrue(first.getStandardFee() >= 0);
    }

    @Test
    @DisplayName("Should retrieve treatment by valid ID")
    void shouldGetTreatmentById() {
        List<Treatment> all = treatmentDAO.getAllTreatments();
        assertFalse(all.isEmpty());

        int targetId = all.get(0).getTreatmentId();
        Treatment treatment = treatmentDAO.getTreatmentById(targetId);

        assertNotNull(treatment);
        assertEquals(targetId, treatment.getTreatmentId());
        assertEquals(all.get(0).getTreatmentName(), treatment.getTreatmentName());
    }

    @Test
    @DisplayName("Should return null for non-existent treatment ID")
    void shouldReturnNullForMissingTreatment() {
        Treatment treatment = treatmentDAO.getTreatmentById(999999);
        assertNull(treatment);
    }

    @Test
    @DisplayName("Should successfully create, update, and delete a treatment record")
    void shouldPerformFullTreatmentCrudLifecycle() {
        // 1. Create
        String uniqueName = "Test Procedure " + System.currentTimeMillis();
        Treatment newTreatment = new Treatment(uniqueName, 3500.50);
        boolean created = treatmentDAO.addTreatment(newTreatment);
        assertTrue(created, "Treatment creation should succeed");
        assertTrue(newTreatment.getTreatmentId() > 0, "Generated key should be set on entity");

        int generatedId = newTreatment.getTreatmentId();

        // 2. Read
        Treatment fetched = treatmentDAO.getTreatmentById(generatedId);
        assertNotNull(fetched);
        assertEquals(uniqueName, fetched.getTreatmentName());
        assertEquals(3500.50, fetched.getStandardFee(), 0.001);

        // 3. Update
        fetched.setTreatmentName(uniqueName + " Updated");
        fetched.setStandardFee(4200.00);
        boolean updated = treatmentDAO.updateTreatment(fetched);
        assertTrue(updated, "Treatment update should succeed");

        Treatment updatedRecord = treatmentDAO.getTreatmentById(generatedId);
        assertNotNull(updatedRecord);
        assertEquals(uniqueName + " Updated", updatedRecord.getTreatmentName());
        assertEquals(4200.00, updatedRecord.getStandardFee(), 0.001);

        // 4. Delete
        boolean deleted = treatmentDAO.deleteTreatment(generatedId);
        assertTrue(deleted, "Treatment deletion should succeed");

        Treatment deletedRecord = treatmentDAO.getTreatmentById(generatedId);
        assertNull(deletedRecord, "Treatment should no longer exist after deletion");
    }

    @Test
    @DisplayName("Should handle null inputs safely in add and update operations")
    void shouldHandleNullInputsSafely() {
        assertFalse(treatmentDAO.addTreatment(null));
        assertFalse(treatmentDAO.updateTreatment(null));
        assertFalse(treatmentDAO.updateTreatment(new Treatment(0, "Invalid", 100.0)));
    }
}
