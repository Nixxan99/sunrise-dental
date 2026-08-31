package com.sunrisedental.dao;

import com.sunrisedental.model.Patient;
import java.util.List;

/**
 * Data Access Object interface for Patient registry.
 */
public interface PatientDAO {

    /**
     * Finds a patient by primary key ID.
     *
     * @param patientId ID of the patient
     * @return Patient if found, null otherwise
     */
    Patient getPatientById(int patientId);

    /**
     * Finds a patient by contact phone number.
     *
     * @param contactNumber patient phone number
     * @return Patient if found, null otherwise
     */
    Patient getPatientByContactNumber(String contactNumber);

    /**
     * Inserts a new patient record into the database.
     *
     * @param patient patient entity to persist
     * @return true if successful, false otherwise
     */
    boolean createPatient(Patient patient);

    /**
     * Retrieves all registered patients.
     *
     * @return list of patients
     */
    List<Patient> getAllPatients();
}
