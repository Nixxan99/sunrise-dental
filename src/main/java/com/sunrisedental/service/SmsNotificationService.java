package com.sunrisedental.service;

import com.sunrisedental.dao.NotificationLogDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.impl.NotificationLogDAOImpl;
import com.sunrisedental.dao.impl.PatientDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Patient;

import java.util.logging.Logger;

/**
 * Concrete Strategy: SMS Notification Service.
 * Formats concise SMS alerts and records audit logs into notification_logs.
 */
public class SmsNotificationService implements NotificationService {

    private static final Logger LOGGER = Logger.getLogger(SmsNotificationService.class.getName());
    private static final String CLINIC_PHONE = "+94 11 234 5678";
    private static final String CLINIC_NAME = "Sunrise Dental Clinic";

    private final NotificationLogDAO logDAO;
    private final PatientDAO patientDAO;

    public SmsNotificationService() {
        this.logDAO = new NotificationLogDAOImpl();
        this.patientDAO = new PatientDAOImpl();
    }

    public SmsNotificationService(NotificationLogDAO logDAO, PatientDAO patientDAO) {
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

        String smsBody = formatAppointmentAlert(appt);

        LOGGER.info("[SMS DISPATCH SIMULATION] Sent to: " + recipientPhone + " | Message: " + smsBody);

        // Record audit trail in database
        try {
            logDAO.logNotification(recipientPhone, smsBody, "SENT_SMS");
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
        String patient = (appt.getPatientName() != null && !appt.getPatientName().isEmpty())
                ? appt.getPatientName() : "Valued Patient";
        String doctor = (appt.getDentistName() != null && !appt.getDentistName().isEmpty())
                ? appt.getDentistName() : "Assigned Doctor";
        String procedure = (appt.getTreatmentName() != null && !appt.getTreatmentName().isEmpty())
                ? appt.getTreatmentName() : "Dental Treatment";

        return String.format(
                "Dear %s, your appointment #%d with %s for %s is confirmed for %s at %s. %s (Tel: %s).",
                patient,
                appt.getAppointmentNumber(),
                doctor,
                procedure,
                appt.getAppointmentDate() != null ? appt.getAppointmentDate().toString() : "Scheduled Date",
                appt.getAppointmentTime() != null ? appt.getAppointmentTime().toString() : "Scheduled Time",
                CLINIC_NAME,
                CLINIC_PHONE
        );
    }
}
