package com.sunrisedental.service.observer;

import com.sunrisedental.model.Appointment;

/**
 * Observer interface for listening to appointment lifecycle events in the application tier.
 * Part of the Observer Pattern implementation avoiding database trigger dependencies in distributed TiDB.
 */
public interface AppointmentRegistrationListener {

    /**
     * Callback triggered when a new appointment is registered successfully in the system.
     *
     * @param appt the newly registered appointment entity
     * @param performedBy user or system context responsible for the operation
     */
    void onAppointmentRegistered(Appointment appt, String performedBy);
}
