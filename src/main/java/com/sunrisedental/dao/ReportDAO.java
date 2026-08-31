package com.sunrisedental.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for Decision-Making and Clinic Management Analytics (CIS6003 Task B).
 */
public interface ReportDAO {

    class TreatmentReportItem implements Serializable {
        private final String treatmentName;
        private final int appointmentCount;
        private final double totalRevenue;
        private final double averageFee;

        public TreatmentReportItem(String treatmentName, int appointmentCount, double totalRevenue, double averageFee) {
            this.treatmentName = treatmentName;
            this.appointmentCount = appointmentCount;
            this.totalRevenue = totalRevenue;
            this.averageFee = averageFee;
        }

        public String getTreatmentName() {
            return treatmentName;
        }

        public int getAppointmentCount() {
            return appointmentCount;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public double getAverageFee() {
            return averageFee;
        }
    }

    class DoctorReportItem implements Serializable {
        private final String doctorName;
        private final String specialization;
        private final int appointmentCount;
        private final double totalRevenue;

        public DoctorReportItem(String doctorName, String specialization, int appointmentCount, double totalRevenue) {
            this.doctorName = doctorName;
            this.specialization = specialization;
            this.appointmentCount = appointmentCount;
            this.totalRevenue = totalRevenue;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public String getSpecialization() {
            return specialization;
        }

        public int getAppointmentCount() {
            return appointmentCount;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }
    }

    List<TreatmentReportItem> getTreatmentBreakdown();

    List<DoctorReportItem> getDoctorBreakdown();

    Map<String, Object> getOverallSummary();
}
