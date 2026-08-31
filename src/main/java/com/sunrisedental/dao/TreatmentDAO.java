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
    boolean addTreatment(Treatment treatment);

    /**
     * Alias for addTreatment.
     */
    boolean createTreatment(Treatment treatment);

    /**
     * Updates an existing treatment record.
     *
     * @param treatment treatment entity with updated values
     * @return true if updated, false otherwise
     */
    boolean updateTreatment(Treatment treatment);

    /**
     * Deletes a treatment record by ID.
     *
     * @param id identifier of the treatment to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteTreatment(int id);
}
