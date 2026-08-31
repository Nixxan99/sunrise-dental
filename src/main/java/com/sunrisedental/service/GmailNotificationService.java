package com.sunrisedental.service;

import com.sunrisedental.dao.NotificationLogDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.impl.NotificationLogDAOImpl;
import com.sunrisedental.dao.impl.PatientDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Patient;

import java.util.logging.Logger;

/**
 * Concrete Strategy: Gmail/Email Notification Service.
 * Formats email alerts and records an audit entry into notification_logs.
 */
public class GmailNotificationService implements NotificationService {

    private static final Logger LOGGER = Logger.getLogger(GmailNotificationService.class.getName());
    private static final String CLINIC_PHONE = "+94 11 234 5678";
    private static final String CLINIC_NAME = "Sunrise Dental Clinic";

    private final NotificationLogDAO logDAO;
    private final PatientDAO patientDAO;

    public GmailNotificationService() {
        this.logDAO = new NotificationLogDAOImpl();
        this.patientDAO = new PatientDAOImpl();
    }

    public GmailNotificationService(NotificationLogDAO logDAO, PatientDAO patientDAO) {
        this.logDAO = logDAO;
        this.patientDAO = patientDAO;
    }

    @Override
    public boolean sendAppointmentAlert(Appointment appt) {
        if (appt == null) {
            return false;
        }

        String recipientPhone = "UNKNOWN";
        try {
            Patient p = patientDAO.getPatientById(appt.getPatientId());
            if (p != null && p.getContactNumber() != null) {
                recipientPhone = p.getContactNumber();
            }
        } catch (Exception ignored) {
        }

        String emailBody = formatAppointmentAlert(appt);
        String recipientEmail = recipientPhone + "@patients.sunrisedental.lk";

        LOGGER.info("[GMAIL/EMAIL DISPATCH SIMULATION] To: " + recipientEmail + " | Content: " + emailBody);

        // Record audit trail in database
        try {
            logDAO.logNotification(recipientPhone, emailBody, "SENT_GMAIL");
        } catch (Exception e) {
            LOGGER.warning("Could not persist notification log: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String formatAppointmentAlert(Appointment appt) {
        if (appt == null) {
            return "";
        }
        String patient = appt.getPatientName() != null ? appt.getPatientName() : "Valued Patient";
        String doctor = appt.getDentistName() != null ? appt.getDentistName() : "Assigned Doctor";
        String procedure = appt.getTreatmentName() != null ? appt.getTreatmentName() : "Dental Treatment";

        return String.format(
                "Subject: Appointment Confirmation - %s (#%d)\n\n"
                        + "Dear %s,\n\n"
                        + "Your dental appointment has been scheduled:\n"
                        + "- Appointment Ref: #%d\n"
                        + "- Attending Doctor: %s\n"
                        + "- Procedure: %s\n"
                        + "- Date: %s\n"
                        + "- Time: %s\n\n"
                        + "Location: 123 Healthway Boulevard, Colombo 03.\n"
                        + "If you need to reschedule, please call %s.\n\n"
                        + "Warm regards,\n%s Team",
                CLINIC_NAME,
                appt.getAppointmentNumber(),
                patient,
                appt.getAppointmentNumber(),
                doctor,
                procedure,
                appt.getAppointmentDate(),
                appt.getAppointmentTime(),
                CLINIC_PHONE,
                CLINIC_NAME
        );
    }
}
