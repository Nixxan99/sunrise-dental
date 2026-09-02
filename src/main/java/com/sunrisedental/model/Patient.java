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
    private String email;
    private String nic;

    /**
     * Default no-argument constructor.
     */
    public Patient() {
    }

    /**
     * Constructor without patientId, email, or nic.
     */
    public Patient(String fullName, String address, String contactNumber) {
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    /**
     * Constructor without patientId with email.
     */
    public Patient(String fullName, String address, String contactNumber, String email) {
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    /**
     * Constructor without patientId with email and nic.
     */
    public Patient(String fullName, String address, String contactNumber, String email, String nic) {
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.nic = nic;
    }

    /**
     * Parameterized constructor without email or nic.
     */
    public Patient(int patientId, String fullName, String address, String contactNumber) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    /**
     * Parameterized constructor including email without nic.
     */
    public Patient(int patientId, String fullName, String address, String contactNumber, String email) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    /**
     * Full parameterized constructor including email and nic.
     */
    public Patient(int patientId, String fullName, String address, String contactNumber, String email, String nic) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.nic = nic;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return patientId == patient.patientId &&
                Objects.equals(fullName, patient.fullName) &&
                Objects.equals(address, patient.address) &&
                Objects.equals(contactNumber, patient.contactNumber) &&
                Objects.equals(email, patient.email) &&
                Objects.equals(nic, patient.nic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, fullName, address, contactNumber, email, nic);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", nic='" + nic + '\'' +
                '}';
    }
}
