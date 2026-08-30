package com.sunrisedental.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO model representing a Dentist in the Sunrise Dental Clinic System.
 */
public class Dentist implements Serializable {

    private static final long serialVersionUID = 1L;

    private int dentistId;
    private String name;
    private String specialization;
    private String contactNumber;

    /**
     * Default no-argument constructor.
     */
    public Dentist() {
    }

    /**
     * Constructor without dentistId (useful for registering a new dentist).
     */
    public Dentist(String name, String specialization, String contactNumber) {
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
    }

    /**
     * Full parameterized constructor.
     */
    public Dentist(int dentistId, String name, String specialization, String contactNumber) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
    }

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
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
        Dentist dentist = (Dentist) o;
        return dentistId == dentist.dentistId &&
                Objects.equals(name, dentist.name) &&
                Objects.equals(specialization, dentist.specialization) &&
                Objects.equals(contactNumber, dentist.contactNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dentistId, name, specialization, contactNumber);
    }

    @Override
    public String toString() {
        return "Dentist{" +
                "dentistId=" + dentistId +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
