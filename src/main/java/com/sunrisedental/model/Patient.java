package com.sunrisedental.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO model representing a Patient in the Sunrise Dental Clinic System.
 */
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    private int patientId;
    private String fullName;
    private String address;
    private String contactNumber;

    /**
     * Default no-argument constructor.
     */
    public Patient() {
    }

    /**
     * Constructor without patientId (useful for new patient registration).
     */
    public Patient(String fullName, String address, String contactNumber) {
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    /**
     * Full parameterized constructor.
     */
    public Patient(int patientId, String fullName, String address, String contactNumber) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return patientId == patient.patientId &&
                Objects.equals(fullName, patient.fullName) &&
                Objects.equals(address, patient.address) &&
                Objects.equals(contactNumber, patient.contactNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, fullName, address, contactNumber);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
