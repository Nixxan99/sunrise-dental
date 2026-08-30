package com.sunrisedental.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO model representing a Dental Treatment/Procedure in the Sunrise Dental Clinic System.
 */
public class Treatment implements Serializable {

    private static final long serialVersionUID = 1L;

    private int treatmentId;
    private String treatmentName;
    private double standardFee;

    /**
     * Default no-argument constructor.
     */
    public Treatment() {
    }

    /**
     * Constructor without treatmentId (useful for adding a new treatment).
     */
    public Treatment(String treatmentName, double standardFee) {
        this.treatmentName = treatmentName;
        this.standardFee = standardFee;
    }

    /**
     * Full parameterized constructor.
     */
    public Treatment(int treatmentId, String treatmentName, double standardFee) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.standardFee = standardFee;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public double getStandardFee() {
        return standardFee;
    }

    public void setStandardFee(double standardFee) {
        this.standardFee = standardFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treatment treatment = (Treatment) o;
        return treatmentId == treatment.treatmentId &&
                Double.compare(treatment.standardFee, standardFee) == 0 &&
                Objects.equals(treatmentName, treatment.treatmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentId, treatmentName, standardFee);
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "treatmentId=" + treatmentId +
                ", treatmentName='" + treatmentName + '\'' +
                ", standardFee=" + standardFee +
                '}';
    }
}
