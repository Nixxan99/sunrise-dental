package com.sunrisedental.dao;

import com.sunrisedental.dao.impl.DentistDAOImpl;
import com.sunrisedental.model.Dentist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit & Integration tests for DentistDAO CRUD operations.
 */
@DisplayName("DentistDAO CRUD Operations and Directory Tests")
class DentistDAOTest {

    private DentistDAO dentistDAO;

    @BeforeEach
    void setUp() {
        dentistDAO = new DentistDAOImpl();
    }

    @Test
    @DisplayName("Should retrieve all registered dentists")
    void shouldGetAllDentists() {
        List<Dentist> dentists = dentistDAO.getAllDentists();
        assertNotNull(dentists, "Dentists list should not be null");
        assertFalse(dentists.isEmpty(), "Directory should contain seed dentists");

        Dentist first = dentists.get(0);
        assertTrue(first.getDentistId() > 0);
        assertNotNull(first.getName());
        assertNotNull(first.getSpecialization());
    }

    @Test
    @DisplayName("Should retrieve dentist by valid ID")
    void shouldGetDentistById() {
        List<Dentist> all = dentistDAO.getAllDentists();
        assertFalse(all.isEmpty());

        int targetId = all.get(0).getDentistId();
        Dentist dentist = dentistDAO.getDentistById(targetId);

        assertNotNull(dentist);
        assertEquals(targetId, dentist.getDentistId());
        assertEquals(all.get(0).getName(), dentist.getName());
    }

    @Test
    @DisplayName("Should return null for non-existent dentist ID")
    void shouldReturnNullForMissingDentist() {
        Dentist dentist = dentistDAO.getDentistById(999999);
        assertNull(dentist);
    }

    @Test
    @DisplayName("Should successfully create, update, and delete a dentist record")
    void shouldPerformFullDentistCrudLifecycle() {
        // 1. Create
        String uniqueName = "Dr. Test Practitioner " + System.currentTimeMillis();
        Dentist newDentist = new Dentist(uniqueName, "Periodontics", "0770000000");
        boolean created = dentistDAO.addDentist(newDentist);
        assertTrue(created, "Dentist creation should succeed");
        assertTrue(newDentist.getDentistId() > 0, "Generated key should be populated");

        int generatedId = newDentist.getDentistId();

        // 2. Read
        Dentist fetched = dentistDAO.getDentistById(generatedId);
        assertNotNull(fetched);
        assertEquals(uniqueName, fetched.getName());
        assertEquals("Periodontics", fetched.getSpecialization());

        // 3. Update
        fetched.setName(uniqueName + " Ph.D.");
        fetched.setSpecialization("Maxillofacial Surgery");
        fetched.setContactNumber("0779999999");
        boolean updated = dentistDAO.updateDentist(fetched);
        assertTrue(updated, "Dentist update should succeed");

        Dentist updatedRecord = dentistDAO.getDentistById(generatedId);
        assertNotNull(updatedRecord);
        assertEquals(uniqueName + " Ph.D.", updatedRecord.getName());
        assertEquals("Maxillofacial Surgery", updatedRecord.getSpecialization());
        assertEquals("0779999999", updatedRecord.getContactNumber());

        // 4. Delete
        boolean deleted = dentistDAO.deleteDentist(generatedId);
        assertTrue(deleted, "Dentist deletion should succeed");

        Dentist deletedRecord = dentistDAO.getDentistById(generatedId);
        assertNull(deletedRecord, "Dentist should no longer exist after deletion");
    }

    @Test
    @DisplayName("Should handle null inputs safely in add and update operations")
    void shouldHandleNullInputsSafely() {
        assertFalse(dentistDAO.addDentist(null));
        assertFalse(dentistDAO.updateDentist(null));
        assertFalse(dentistDAO.updateDentist(new Dentist(0, "Invalid", "Specialist", "123")));
    }
}
