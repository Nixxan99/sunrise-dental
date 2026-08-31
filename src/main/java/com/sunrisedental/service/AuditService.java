package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentAuditDAO;
import com.sunrisedental.dao.impl.AppointmentAuditDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.service.observer.AppointmentRegistrationListener;

import java.util.logging.Logger;

/**
 * Service providing application-tier auditing for clinic operations (Observer Pattern).
 * Shifting audit responsibilities from database triggers to application tier ensures
 * compatibility with cloud-native distributed databases like TiDB.
 */
public class AuditService implements AppointmentRegistrationListener {

    private static final Logger LOGGER = Logger.getLogger(AuditService.class.getName());

    private final AppointmentAuditDAO auditDAO;

    public AuditService() {
        this.auditDAO = new AppointmentAuditDAOImpl();
    }

    public AuditService(AppointmentAuditDAO auditDAO) {
        this.auditDAO = auditDAO;
    }

    @Override
    public void onAppointmentRegistered(Appointment appt, String performedBy) {
        if (appt == null) {
            return;
        }

        String details = String.format(
                "Appointment #%d booked for Patient ID: %d, Dentist ID: %d, Treatment ID: %d, Date: %s, Time: %s, Status: %s",
                appt.getAppointmentNumber(),
                appt.getPatientId(),
                appt.getDentistId(),
                appt.getTreatmentId(),
                appt.getAppointmentDate(),
                appt.getAppointmentTime(),
                appt.getStatus()
        );

        String user = (performedBy != null && !performedBy.trim().isEmpty()) ? performedBy.trim() : "SYSTEM";

        LOGGER.info(String.format("[AUDIT LOG OBSERVER] Action: APPOINTMENT_REGISTERED | Appt: #%d | By: %s",
                appt.getAppointmentNumber(), user));

        auditDAO.logAppointmentAudit(appt.getAppointmentNumber(), "APPOINTMENT_REGISTERED", user, details);
    }
}
