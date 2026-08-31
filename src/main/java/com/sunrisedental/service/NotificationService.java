package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;

/**
 * Strategy interface for patient notification services (CIS6003 Task B).
 */
public interface NotificationService {

    /**
     * Sends an appointment confirmation alert to the patient and logs the event in audit logs.
     *
     * @param appt the registered appointment entity
     * @return true if formatted and dispatched (or simulated) successfully; false otherwise
     */
    boolean sendAppointmentAlert(Appointment appt);

    /**
     * Formats the human-readable notification text payload for display and auditing.
     *
     * @param appt the appointment entity
     * @return formatted message string
     */
    String formatAppointmentAlert(Appointment appt);
}
