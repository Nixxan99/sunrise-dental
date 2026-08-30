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
    boolean createDentist(Dentist dentist);
}
