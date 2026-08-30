package com.sunrisedental.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * POJO model representing an Invoice / Bill in the Sunrise Dental Clinic System.
 */
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    private int billId;
    private int appointmentNumber;
    private double consultationFee;
    private double treatmentCost;
    private double totalAmount;
    private String paymentStatus;
    private LocalDateTime issuedAt;

    /**
     * Default no-argument constructor.
     */
    public Bill() {
    }

    /**
     * Constructor for generating a new bill with auto-calculated total and timestamp.
     */
    public Bill(int appointmentNumber, double consultationFee, double treatmentCost, String paymentStatus) {
        this.appointmentNumber = appointmentNumber;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = consultationFee + treatmentCost;
        this.paymentStatus = paymentStatus;
        this.issuedAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor without billId (for inserts with existing total and issuedAt).
     */
    public Bill(int appointmentNumber, double consultationFee, double treatmentCost,
                double totalAmount, String paymentStatus, LocalDateTime issuedAt) {
        this.appointmentNumber = appointmentNumber;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.issuedAt = issuedAt;
    }

    /**
     * Full parameterized constructor.
     */
    public Bill(int billId, int appointmentNumber, double consultationFee, double treatmentCost,
                double totalAmount, String paymentStatus, LocalDateTime issuedAt) {
        this.billId = billId;
        this.appointmentNumber = appointmentNumber;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.issuedAt = issuedAt;
    }

    /**
     * Calculates and updates totalAmount based on consultationFee and treatmentCost.
     *
     * @return the calculated total amount
     */
    public double calculateTotal() {
        this.totalAmount = this.consultationFee + this.treatmentCost;
        return this.totalAmount;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * Convenience setter for java.sql.Timestamp (from JDBC ResultSet).
     */
    public void setIssuedAt(Timestamp timestamp) {
        this.issuedAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }

    /**
     * Convenience setter from ISO-8601 String representation.
     */
    public void setIssuedAt(String dateTimeStr) {
        if (dateTimeStr != null && !dateTimeStr.trim().isEmpty()) {
            try {
                this.issuedAt = LocalDateTime.parse(dateTimeStr.trim());
            } catch (DateTimeParseException e) {
                this.issuedAt = null;
            }
        } else {
            this.issuedAt = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return billId == bill.billId &&
                appointmentNumber == bill.appointmentNumber &&
                Double.compare(bill.consultationFee, consultationFee) == 0 &&
                Double.compare(bill.treatmentCost, treatmentCost) == 0 &&
                Double.compare(bill.totalAmount, totalAmount) == 0 &&
                Objects.equals(paymentStatus, bill.paymentStatus) &&
                Objects.equals(issuedAt, bill.issuedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, appointmentNumber, consultationFee, treatmentCost,
                totalAmount, paymentStatus, issuedAt);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", appointmentNumber=" + appointmentNumber +
                ", consultationFee=" + consultationFee +
                ", treatmentCost=" + treatmentCost +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
