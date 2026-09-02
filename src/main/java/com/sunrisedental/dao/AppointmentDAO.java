package com.sunrisedental.dao;

import com.sunrisedental.model.Appointment;
import java.util.List;

/**
 * Data Access Object interface for Appointment management.
 */
public interface AppointmentDAO {

    /**
     * Inserts a new appointment record into the database.
     *
     * @param appt appointment entity to register
     * @return true if successfully inserted, false otherwise
     */
    boolean registerAppointment(Appointment appt);

    /**
     * Retrieves an appointment by its number (string format), including
     * joined patient, dentist, and treatment presentation details.
     *
     * @param apptNumber appointment number as string
     * @return populated Appointment entity or null if not found
     */
    Appointment getAppointmentByNumber(String apptNumber);

    /**
     * Retrieves an appointment by its numeric identifier, including joined details.
     *
     * @param apptNumber appointment number
     * @return populated Appointment entity or null if not found
     */
    Appointment getAppointmentByNumber(int apptNumber);

    /**
     * Retrieves all appointments for dashboard display with joined details.
     *
     * @return list of all appointments
     */
    List<Appointment> getAllAppointments();

    /**
     * Universal search matching appointment number, patient name, patient contact number, or patient NIC.
     *
     * @param term search term
     * @return list of matching appointments with joined details
     */
    List<Appointment> searchAppointmentsUniversal(String term);

    /**
     * Updates status of an appointment (e.g., SCHEDULED, COMPLETED, CANCELLED).
     *
     * @param appointmentNumber target appointment number
     * @param status new status
     * @return true if updated, false otherwise
     */
    boolean updateStatus(int appointmentNumber, String status);
}
