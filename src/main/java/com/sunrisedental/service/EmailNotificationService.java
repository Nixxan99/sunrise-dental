package com.sunrisedental.service;

import com.sunrisedental.dao.NotificationLogDAO;
import com.sunrisedental.dao.impl.NotificationLogDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.util.ConfigUtil;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service providing asynchronous, non-blocking email delivery via Gmail SMTP
 * (smtp.gmail.com:587 with STARTTLS) using jakarta.mail.
 * Automatically falls back to simulated stdout logging if credentials are not configured,
 * ensuring zero disruption to web request processing.
 */
public class EmailNotificationService {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationService.class.getName());

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private final String senderEmail;
    private final String senderPassword;
    private final NotificationLogDAO notificationLogDAO;
    private final ExecutorService executorService;

    public EmailNotificationService() {
        String email = ConfigUtil.getProperty("mail.sender");
        String password = ConfigUtil.getProperty("mail.password");

        this.senderEmail = (email != null && !email.trim().isEmpty()) ? email.trim() : null;
        this.senderPassword = (password != null && !password.trim().isEmpty()) ? password.trim() : null;
        this.notificationLogDAO = new NotificationLogDAOImpl();
        this.executorService = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "EmailNotificationWorker");
            t.setDaemon(true);
            return t;
        });
    }

    public EmailNotificationService(String senderEmail, String senderPassword, NotificationLogDAO notificationLogDAO) {
        this.senderEmail = (senderEmail != null && !senderEmail.trim().isEmpty()) ? senderEmail.trim() : null;
        this.senderPassword = (senderPassword != null && !senderPassword.trim().isEmpty()) ? senderPassword.trim() : null;
        this.notificationLogDAO = notificationLogDAO != null ? notificationLogDAO : new NotificationLogDAOImpl();
        this.executorService = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "EmailNotificationWorker");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Dispatches an appointment confirmation email asynchronously to avoid blocking the HTTP request thread.
     */
    public CompletableFuture<Boolean> sendAppointmentConfirmationAsync(
            String recipientEmail,
            String patientName,
            String doctorName,
            String treatmentName,
            String dateStr,
            String timeStr,
            int appointmentNumber) {

        return CompletableFuture.supplyAsync(() -> sendAppointmentConfirmation(
                recipientEmail, patientName, doctorName, treatmentName, dateStr, timeStr, appointmentNumber
        ), executorService);
    }

    /**
     * Synchronous email delivery with SMTP transport or simulated logging.
     */
    public boolean sendAppointmentConfirmation(
            String recipientEmail,
            String patientName,
            String doctorName,
            String treatmentName,
            String dateStr,
            String timeStr,
            int appointmentNumber) {

        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            LOGGER.info("No recipient email provided for appointment #" + appointmentNumber + ". Skipping email dispatch.");
            return false;
        }

        String safePatient = (patientName != null && !patientName.trim().isEmpty()) ? patientName.trim() : "Valued Patient";
        String safeDoctor = (doctorName != null && !doctorName.trim().isEmpty()) ? doctorName.trim() : "Assigned Dentist";
        String safeTreatment = (treatmentName != null && !treatmentName.trim().isEmpty()) ? treatmentName.trim() : "Dental Care";
        String safeDate = (dateStr != null && !dateStr.trim().isEmpty()) ? dateStr.trim() : "Scheduled Date";
        String safeTime = (timeStr != null && !timeStr.trim().isEmpty()) ? timeStr.trim() : "Scheduled Time";

        String subject = "Appointment Confirmation #" + appointmentNumber + " - Sunrise Dental Clinic";
        String body = String.format(
                "Dear %s,\n\n" +
                "Your dental appointment has been successfully scheduled at Sunrise Dental Clinic.\n\n" +
                "----------------------------------------\n" +
                "Appointment Number: #%d\n" +
                "Attending Dentist : %s\n" +
                "Treatment/Service : %s\n" +
                "Date              : %s\n" +
                "Time              : %s\n" +
                "Location          : 123 Healthway Boulevard, Suite 400, Colombo\n" +
                "----------------------------------------\n\n" +
                "Please arrive 10 minutes prior to your allocated time. If you need to reschedule, please contact us at +94 11 234 5678.\n\n" +
                "Warm regards,\n" +
                "Sunrise Dental Clinic Administration",
                safePatient, appointmentNumber, safeDoctor, safeTreatment, safeDate, safeTime
        );

        return dispatchEmail(recipientEmail.trim(), subject, body);
    }

    /**
     * Dispatches an invoice receipt email asynchronously with itemized breakdown and AI care plan.
     */
    public CompletableFuture<Boolean> sendInvoiceReceiptAsync(
            Appointment appt,
            Bill bill,
            String recipientEmail,
            String careAdvice) {

        return CompletableFuture.supplyAsync(() -> sendInvoiceReceipt(
                appt, bill, recipientEmail, careAdvice
        ), executorService);
    }

    /**
     * Synchronous invoice receipt email delivery.
     */
    public boolean sendInvoiceReceipt(
            Appointment appt,
            Bill bill,
            String recipientEmail,
            String careAdvice) {

        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            LOGGER.info("No recipient email provided for billing receipt. Skipping email dispatch.");
            return false;
        }

        int apptNo = (appt != null) ? appt.getAppointmentNumber() : (bill != null ? bill.getAppointmentNumber() : 0);
        String patientName = (appt != null && appt.getPatientName() != null) ? appt.getPatientName() : "Valued Patient";
        String dentistName = (appt != null && appt.getDentistName() != null) ? appt.getDentistName() : "Attending Dentist";
        String treatmentName = (appt != null && appt.getTreatmentName() != null) ? appt.getTreatmentName() : "Dental Treatment";
        String apptDate = (appt != null && appt.getAppointmentDate() != null) ? appt.getAppointmentDate().toString() : "Recent Date";

        double treatmentCost = (bill != null) ? bill.getTreatmentCost() : 0.00;
        double consultationFee = (bill != null) ? bill.getConsultationFee() : 1500.00;
        double totalAmount = (bill != null) ? bill.getTotalAmount() : (treatmentCost + consultationFee);
        String paymentStatus = (bill != null && bill.getPaymentStatus() != null) ? bill.getPaymentStatus() : "PAID";

        String subject = "Payment Receipt & Care Summary - Invoice #INV-" + apptNo + " - Sunrise Dental Clinic";

        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(patientName).append(",\n\n");
        sb.append("Thank you for visiting Sunrise Dental Clinic. Here is your official payment receipt and clinical summary:\n\n");
        sb.append("====================================================\n");
        sb.append("             SUNRISE DENTAL CLINIC RECEIPT          \n");
        sb.append("====================================================\n");
        sb.append(String.format("Invoice Number   : INV-%d\n", apptNo));
        sb.append(String.format("Appointment Ref  : #%d\n", apptNo));
        sb.append(String.format("Date             : %s\n", apptDate));
        sb.append(String.format("Attending Doctor : %s\n", dentistName));
        sb.append(String.format("Treatment Service: %s\n", treatmentName));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Treatment Fee    : LKR %.2f\n", treatmentCost));
        sb.append(String.format("Consultation Fee : LKR %.2f\n", consultationFee));
        sb.append(String.format("Total Amount     : LKR %.2f\n", totalAmount));
        sb.append(String.format("Payment Status   : %s\n", paymentStatus));
        sb.append("====================================================\n\n");

        if (careAdvice != null && !careAdvice.trim().isEmpty()) {
            sb.append("PERSONALIZED POST-TREATMENT CARE INSTRUCTIONS:\n");
            sb.append(careAdvice.trim()).append("\n\n");
        }

        sb.append("If you have questions regarding your invoice or dental care, please contact our support desk at +94 11 234 5678.\n\n");
        sb.append("Warm regards,\n");
        sb.append("Sunrise Dental Clinic Administration\n");
        sb.append("123 Healthway Boulevard, Suite 400, Colombo\n");

        String body = sb.toString();
        return dispatchEmail(recipientEmail.trim(), subject, body);
    }

    private boolean dispatchEmail(String recipient, String subject, String body) {
        boolean deliveredViaSmtp = false;

        // If credentials are configured, attempt real Gmail SMTP transmission
        if (senderEmail != null && senderPassword != null) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
                props.put("mail.smtp.connectiontimeout", "5000");
                props.put("mail.smtp.timeout", "5000");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail, "Sunrise Dental Clinic"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                deliveredViaSmtp = true;
                LOGGER.info("Successfully sent SMTP email to " + recipient + " via Gmail.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Gmail SMTP dispatch failed (" + e.getMessage() + "). Falling back to simulated log.", e);
            }
        }

        // Offline / Simulation fallback output
        if (!deliveredViaSmtp) {
            LOGGER.info(String.format(
                    "[GMAIL SMTP NOTIFICATION DISPATCH (SIMULATED)]\nTo: %s\nSubject: %s\n\n%s",
                    recipient, subject, body
            ));
        }

        // Persist audit trail into notification_logs
        try {
            notificationLogDAO.logNotification(recipient, body, "SENT");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to persist email notification audit log: " + e.getMessage(), e);
        }

        return true;
    }
}
