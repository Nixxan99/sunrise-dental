package com.sunrisedental.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * POJO model representing an Appointment in the Sunrise Dental Clinic System.
 * Includes core entity fields and nested/joined presentation fields for UI/reporting.
 */
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    // Core entity fields
    private int appointmentNumber;
    private int patientId;
    private int dentistId;
    private int treatmentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    // Nested / Joined presentation fields
    private String patientName;
    private String dentistName;
    private String treatmentName;
    private double cost;

    /**
     * Default no-argument constructor.
     */
    public Appointment() {
    }

    /**
     * Constructor for creating a new appointment (without appointmentNumber and presentation fields).
     */
    public Appointment(int patientId, int dentistId, int treatmentId,
                       LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    /**
     * Constructor for core fields including appointmentNumber.
     */
    public Appointment(int appointmentNumber, int patientId, int dentistId, int treatmentId,
                       LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.appointmentNumber = appointmentNumber;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    /**
     * Full presentation constructor including joined details.
     */
    public Appointment(int appointmentNumber, int patientId, int dentistId, int treatmentId,
                       LocalDate appointmentDate, LocalTime appointmentTime, String status,
                       String patientName, String dentistName, String treatmentName, double cost) {
        this.appointmentNumber = appointmentNumber;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.treatmentName = treatmentName;
        this.cost = cost;
    }

    // --- Core Field Getters and Setters ---

    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * Convenience setter for LocalDate from String (e.g., "YYYY-MM-DD").
     */
    public void setAppointmentDate(String dateStr) {
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                this.appointmentDate = LocalDate.parse(dateStr.trim());
            } catch (DateTimeParseException e) {
                this.appointmentDate = null;
            }
        } else {
            this.appointmentDate = null;
        }
    }

    /**
     * Convenience setter for LocalDate from java.sql.Date.
     */
    public void setAppointmentDate(Date sqlDate) {
        this.appointmentDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    /**
     * Convenience setter for LocalTime from String (e.g., "HH:mm" or "HH:mm:ss").
     */
    public void setAppointmentTime(String timeStr) {
        if (timeStr != null && !timeStr.trim().isEmpty()) {
            try {
                String clean = timeStr.trim();
                if (clean.length() == 5) { // HH:mm -> HH:mm:00
                    clean = clean + ":00";
                }
                this.appointmentTime = LocalTime.parse(clean);
            } catch (DateTimeParseException e) {
                this.appointmentTime = null;
            }
        } else {
            this.appointmentTime = null;
        }
    }

    /**
     * Convenience setter for LocalTime from java.sql.Time.
     */
    public void setAppointmentTime(Time sqlTime) {
        this.appointmentTime = (sqlTime != null) ? sqlTime.toLocalTime() : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Presentation / Joined Field Getters and Setters ---

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return appointmentNumber == that.appointmentNumber &&
                patientId == that.patientId &&
                dentistId == that.dentistId &&
                treatmentId == that.treatmentId &&
                Objects.equals(appointmentDate, that.appointmentDate) &&
                Objects.equals(appointmentTime, that.appointmentTime) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentNumber, patientId, dentistId, treatmentId,
                appointmentDate, appointmentTime, status);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentNumber=" + appointmentNumber +
                ", patientId=" + patientId +
                ", dentistId=" + dentistId +
                ", treatmentId=" + treatmentId +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", status='" + status + '\'' +
                ", patientName='" + patientName + '\'' +
                ", dentistName='" + dentistName + '\'' +
                ", treatmentName='" + treatmentName + '\'' +
                ", cost=" + cost +
                '}';
    }
}
