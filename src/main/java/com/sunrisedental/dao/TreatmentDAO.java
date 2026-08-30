package com.sunrisedental.dao;

import com.sunrisedental.model.Treatment;
import java.util.List;

/**
 * Data Access Object interface for Treatment catalog operations.
 */
public interface TreatmentDAO {

    /**
     * Retrieves all available treatments and standard fees.
     *
     * @return list of treatments
     */
    List<Treatment> getAllTreatments();

    /**
     * Finds a treatment by its unique id.
     *
     * @param id treatment identifier
     * @return Treatment if found, null otherwise
     */
    Treatment getTreatmentById(int id);

    /**
     * Inserts a new treatment record.
     *
     * @param treatment treatment entity to persist
     * @return true if inserted, false otherwise
     */
    boolean createTreatment(Treatment treatment);
}
