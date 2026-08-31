package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Notification Service implementing Strategy and Observer design patterns (CIS6003 Task B).
 * Handles automated SMS and Email alerts for patient appointment bookings and updates.
 */
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private static final String CLINIC_PHONE = "+94 11 234 5678";
    private static final String CLINIC_NAME = "Sunrise Dental Clinic";

    // Observer Pattern: Listener list for appointment events
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public NotificationService() {
        // Register default SMS and Email delivery strategies as observers
        registerObserver(new SmsNotificationStrategy());
        registerObserver(new EmailNotificationStrategy());
    }

    /**
     * Observer interface for appointment status and booking events.
     */
    public interface AppointmentObserver {
        NotificationResult onAppointmentBooked(Appointment appointment, String contactNumber);
    }

    /**
     * Delivery Result Model encapsulating dispatch status and formatted payload.
     */
    public static class NotificationResult implements Serializable {
        private final String channel;
        private final String recipient;
        private final String message;
        private final boolean delivered;
        private final LocalDateTime timestamp;

        public NotificationResult(String channel, String recipient, String message, boolean delivered) {
            this.channel = channel;
            this.recipient = recipient;
            this.message = message;
            this.delivered = delivered;
            this.timestamp = LocalDateTime.now();
        }

        public String getChannel() {
            return channel;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getMessage() {
            return message;
        }

        public boolean isDelivered() {
            return delivered;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getFormattedTimestamp() {
            return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * Strategy 1: SMS Notification Strategy
     */
    public static class SmsNotificationStrategy implements AppointmentObserver {
        @Override
        public NotificationResult onAppointmentBooked(Appointment appointment, String contactNumber) {
            String smsBody = formatSmsMessage(appointment);
            LOGGER.info("[SMS DISPATCH SIMULATION] Sent to " + contactNumber + ": " + smsBody);
            return new NotificationResult("SMS", contactNumber, smsBody, true);
        }

        public String formatSmsMessage(Appointment appt) {
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

    /**
     * Strategy 2: Email Notification Strategy
     */
    public static class EmailNotificationStrategy implements AppointmentObserver {
        @Override
        public NotificationResult onAppointmentBooked(Appointment appointment, String contactNumber) {
            String emailBody = formatEmailMessage(appointment);
            String recipientEmail = contactNumber + "@patients.sunrisedental.lk";
            LOGGER.info("[EMAIL DISPATCH SIMULATION] Sent to " + recipientEmail);
            return new NotificationResult("EMAIL", recipientEmail, emailBody, true);
        }

        public String formatEmailMessage(Appointment appt) {
            return String.format(
                    "Subject: Appointment Confirmation - %s (#%d)\n\n"
                            + "Dear %s,\n\n"
                            + "This is a confirmation of your dental appointment:\n"
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
                    appt.getPatientName() != null ? appt.getPatientName() : "Valued Patient",
                    appt.getAppointmentNumber(),
                    appt.getDentistName() != null ? appt.getDentistName() : "Doctor",
                    appt.getTreatmentName() != null ? appt.getTreatmentName() : "Procedure",
                    appt.getAppointmentDate(),
                    appt.getAppointmentTime(),
                    CLINIC_PHONE,
                    CLINIC_NAME
            );
        }
    }

    public void registerObserver(AppointmentObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(AppointmentObserver observer) {
        observers.remove(observer);
    }

    /**
     * Triggers notifications to all registered observers.
     *
     * @param appointment appointment entity
     * @param contactNumber recipient phone number
     * @return list of notification results
     */
    public List<NotificationResult> notifyAppointmentBooked(Appointment appointment, String contactNumber) {
        if (appointment == null) {
            return Collections.emptyList();
        }
        List<NotificationResult> results = new ArrayList<>();
        for (AppointmentObserver observer : observers) {
            NotificationResult res = observer.onAppointmentBooked(appointment, contactNumber);
            results.add(res);
        }
        return results;
    }

    /**
     * Formats an SMS notification message directly.
     */
    public String formatSmsNotification(Appointment appointment) {
        return new SmsNotificationStrategy().formatSmsMessage(appointment);
    }
}
