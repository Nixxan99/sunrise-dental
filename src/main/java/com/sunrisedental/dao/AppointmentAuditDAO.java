package com.sunrisedental.dao;

/**
 * DAO interface for logging appointment audit events in the application tier (CIS6003 Architecture).
 */
public interface AppointmentAuditDAO {

    /**
     * Inserts an audit log entry for an appointment operation.
     *
     * @param appointmentNumber unique appointment reference
     * @param actionType action name (e.g. APPOINTMENT_REGISTERED, APPOINTMENT_CANCELLED)
     * @param performedBy user or system identifier
     * @param details descriptive audit information (e.g. patient, doctor, date/time)
     * @return true if persisted successfully, false otherwise
     */
    boolean logAppointmentAudit(int appointmentNumber, String actionType, String performedBy, String details);
}
