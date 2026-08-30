package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.util.ValidationUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service handling billing calculations, validations, and receipt generation
 * for the Sunrise Dental Clinic System.
 */
public class BillingService {

    private static final DateTimeFormatter RECEIPT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Calculates the total cost for a dental visit: Treatment Cost + Consultation Fee.
     *
     * @param treatmentCost   cost of the specific dental procedure/treatment
     * @param consultationFee doctor's consultation fee
     * @return sum of treatment cost and consultation fee
     * @throws IllegalArgumentException if either fee is negative
     */
    public double calculateTotalCost(double treatmentCost, double consultationFee) {
        if (!ValidationUtil.isNonNegative(treatmentCost)) {
            throw new IllegalArgumentException("Treatment cost cannot be negative: " + treatmentCost);
        }
        if (!ValidationUtil.isNonNegative(consultationFee)) {
            throw new IllegalArgumentException("Consultation fee cannot be negative: " + consultationFee);
        }
        return treatmentCost + consultationFee;
    }

    /**
     * Constructs and initializes a new Bill entity with validated fees and computed total.
     *
     * @param appointmentNumber unique appointment number
     * @param consultationFee   consultation fee
     * @param treatmentCost     treatment cost
     * @param paymentStatus     e.g., PAID, PENDING, UNPAID
     * @return fully initialized Bill object
     * @throws IllegalArgumentException if consultation fee or treatment cost is negative
     */
    public Bill createBill(int appointmentNumber, double consultationFee, double treatmentCost, String paymentStatus) {
        double total = calculateTotalCost(treatmentCost, consultationFee);
        Bill bill = new Bill();
        bill.setAppointmentNumber(appointmentNumber);
        bill.setConsultationFee(consultationFee);
        bill.setTreatmentCost(treatmentCost);
        bill.setTotalAmount(total);
        bill.setPaymentStatus(paymentStatus != null ? paymentStatus : "PAID");
        bill.setIssuedAt(LocalDateTime.now());
        return bill;
    }

    /**
     * Generates a printable, formatted receipt for a bill.
     *
     * @param bill the bill to generate receipt for
     * @return formatted receipt text
     */
    public String generateReceipt(Bill bill) {
        return generateReceipt(bill, null);
    }

    /**
     * Generates a detailed printable receipt including appointment and patient details.
     *
     * @param bill        the bill details
     * @param appointment associated appointment details (can be null)
     * @return formatted receipt string
     * @throws IllegalArgumentException if bill is null
     */
    public String generateReceipt(Bill bill, Appointment appointment) {
        if (bill == null) {
            throw new IllegalArgumentException("Bill cannot be null for receipt generation");
        }

        String patientName = (appointment != null && appointment.getPatientName() != null)
                ? appointment.getPatientName() : "N/A";
        String dentistName = (appointment != null && appointment.getDentistName() != null)
                ? appointment.getDentistName() : "N/A";
        String treatmentName = (appointment != null && appointment.getTreatmentName() != null)
                ? appointment.getTreatmentName() : "N/A";

        String formattedDate = (bill.getIssuedAt() != null)
                ? bill.getIssuedAt().format(RECEIPT_DATE_FORMAT)
                : LocalDateTime.now().format(RECEIPT_DATE_FORMAT);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("           SUNRISE DENTAL CLINIC - RECEIPT          \n");
        sb.append("====================================================\n");
        sb.append(String.format(Locale.US, "Invoice / Bill ID:     #%d%n", bill.getBillId()));
        sb.append(String.format(Locale.US, "Appointment Number:    #%d%n", bill.getAppointmentNumber()));
        sb.append(String.format(Locale.US, "Patient Name:          %s%n", patientName));
        sb.append(String.format(Locale.US, "Dentist / Doctor:      %s%n", dentistName));
        sb.append(String.format(Locale.US, "Treatment Procedure:   %s%n", treatmentName));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(Locale.US, "Consultation Fee:      $%.2f%n", bill.getConsultationFee()));
        sb.append(String.format(Locale.US, "Treatment Cost:        $%.2f%n", bill.getTreatmentCost()));
        sb.append(String.format(Locale.US, "Total Amount Due:      $%.2f%n", bill.getTotalAmount()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(Locale.US, "Payment Status:        %s%n", bill.getPaymentStatus()));
        sb.append(String.format(Locale.US, "Date Issued:           %s%n", formattedDate));
        sb.append("====================================================\n");
        sb.append("      Thank you for choosing Sunrise Dental!        \n");
        sb.append("====================================================\n");

        return sb.toString();
    }
}
