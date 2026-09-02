package com.sunrisedental.dao;

import com.sunrisedental.model.Patient;
import com.sunrisedental.model.PatientAppointmentHistoryItem;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Patient registry, history tracking, and retention analytics.
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
     * Registers a new patient record and returns the generated primary key ID.
     * If the patient exists by contact number, updates demographic details (including email and NIC) and returns the existing ID.
     *
     * @param patient patient entity to register
     * @return generated or existing patient_id (>0 on success, -1 on failure)
     */
    int registerPatient(Patient patient);

    /**
     * Updates an existing patient's full name, address, contact number, email, and NIC.
     *
     * @param patient patient entity with updated details
     * @return true if updated successfully, false otherwise
     */
    boolean updatePatient(Patient patient);

    /**
     * Safely deletes a patient by ID if no blocking constraints exist.
     *
     * @param patientId ID of the patient to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deletePatient(int patientId);

    /**
     * Retrieves all registered patients ordered by name.
     *
     * @return list of patients
     */
    List<Patient> getAllPatients();

    /**
     * Searches patients by full name, contact number, or email (case-insensitive substring match).
     *
     * @param query search query
     * @return matching patients list
     */
    List<Patient> searchPatients(String query);

    /**
     * Universal search querying across full_name, nic, contact_number, email, or patient_id.
     *
     * @param term multi-parameter search term
     * @return matching patients list
     */
    List<Patient> searchPatientsUniversal(String term);

    /**
     * Retrieves the complete clinical appointment history for a given patient,
     * including attending dentist, treatment procedure, and billing payment status.
     *
     * @param patientId ID of the patient
     * @return list of historical and upcoming appointments
     */
    List<PatientAppointmentHistoryItem> getPatientAppointmentHistory(int patientId);

    /**
     * Calculates patient retention and lifecycle analytics across the system:
     * - totalUniquePatients (distinct patient count)
     * - repeatPatients (patients with > 1 appointment)
     * - newPatientsThisMonth (first booked in current month)
     * - repeatRate (percentage of repeat customers)
     * - retentionRatio
     *
     * @return map of key metrics
     */
    Map<String, Object> getPatientAnalytics();
}
