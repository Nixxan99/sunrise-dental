package com.sunrisedental.dao;

import com.sunrisedental.model.Dentist;
import java.util.List;

/**
 * Data Access Object interface for Dentist directory operations.
 */
public interface DentistDAO {

    /**
     * Retrieves all registered dentists with specializations and contact numbers.
     *
     * @return list of dentists
     */
    List<Dentist> getAllDentists();

    /**
     * Finds a dentist by their unique id.
     *
     * @param dentistId dentist identifier
     * @return Dentist if found, null otherwise
     */
    Dentist getDentistById(int dentistId);

    /**
     * Inserts a new dentist record.
     *
     * @param dentist dentist entity to persist
     * @return true if inserted, false otherwise
     */
    boolean addDentist(Dentist dentist);

    /**
     * Alias for addDentist.
     */
    boolean createDentist(Dentist dentist);

    /**
     * Updates an existing dentist record.
     *
     * @param dentist dentist entity with updated details
     * @return true if updated, false otherwise
     */
    boolean updateDentist(Dentist dentist);

    /**
     * Deletes a dentist record by ID.
     *
     * @param dentistId identifier of the dentist to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteDentist(int dentistId);
}
