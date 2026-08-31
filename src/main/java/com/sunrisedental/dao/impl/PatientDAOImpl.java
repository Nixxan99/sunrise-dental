package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.PatientAppointmentHistoryItem;
import com.sunrisedental.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object implementation for Patient management, history tracking, and retention analytics.
 */
public class PatientDAOImpl implements PatientDAO {

    private static final Logger LOGGER = Logger.getLogger(PatientDAOImpl.class.getName());
    private static volatile boolean schemaVerified = false;

    public PatientDAOImpl() {
        ensureSchema();
    }

    /**
     * Ensures the email column exists in the patients table for backward-compatible schema migration.
     */
    private static synchronized void ensureSchema() {
        if (schemaVerified) return;
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE patients ADD COLUMN IF NOT EXISTS email VARCHAR(100)");
            schemaVerified = true;
        } catch (Exception e) {
            // Ignore if column already exists or IF NOT EXISTS syntax variance
            schemaVerified = true;
            LOGGER.log(Level.FINE, "Schema check on patients email column: " + e.getMessage());
        }
    }

    @Override
    public Patient getPatientById(int patientId) {
        String sql = "SELECT patient_id, full_name, address, contact_number, email FROM patients WHERE patient_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error finding patient by id: " + patientId, e);
        }
        return null;
    }

    @Override
    public Patient getPatientByContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT patient_id, full_name, address, contact_number, email FROM patients WHERE contact_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contactNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error finding patient by contact: " + contactNumber, e);
        }
        return null;
    }

    @Override
    public boolean createPatient(Patient patient) {
        return registerPatient(patient) > 0;
    }

    @Override
    public int registerPatient(Patient patient) {
        if (patient == null) {
            return -1;
        }

        // Check if patient already exists by contact number
        Patient existing = getPatientByContactNumber(patient.getContactNumber());
        if (existing != null) {
            // Update profile info if changed
            existing.setFullName(patient.getFullName());
            if (patient.getAddress() != null && !patient.getAddress().trim().isEmpty()) {
                existing.setAddress(patient.getAddress());
            }
            if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
                existing.setEmail(patient.getEmail());
            }
            updatePatient(existing);
            patient.setPatientId(existing.getPatientId());
            return existing.getPatientId();
        }

        String sql = "INSERT INTO patients (full_name, address, contact_number, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patient.getFullName());
            ps.setString(2, patient.getAddress() != null ? patient.getAddress() : "");
            ps.setString(3, patient.getContactNumber() != null ? patient.getContactNumber().trim() : "");
            ps.setString(4, patient.getEmail() != null ? patient.getEmail().trim() : "");

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int genId = generatedKeys.getInt(1);
                        patient.setPatientId(genId);
                        return genId;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error registering patient: " + patient.getFullName(), e);
        }
        return -1;
    }

    @Override
    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getPatientId() <= 0) {
            return false;
        }
        String sql = "UPDATE patients SET full_name = ?, address = ?, contact_number = ?, email = ? WHERE patient_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patient.getFullName());
            ps.setString(2, patient.getAddress());
            ps.setString(3, patient.getContactNumber());
            ps.setString(4, patient.getEmail() != null ? patient.getEmail().trim() : "");
            ps.setInt(5, patient.getPatientId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error updating patient id: " + patient.getPatientId(), e);
            return false;
        }
    }

    @Override
    public boolean deletePatient(int patientId) {
        if (patientId <= 0) {
            return false;
        }

        // Clean up or check appointments if foreign key RESTRICT exists
        String deleteBillsSql = "DELETE FROM bills WHERE appointment_number IN (SELECT appointment_number FROM appointments WHERE patient_id = ?)";
        String deleteApptsSql = "DELETE FROM appointments WHERE patient_id = ?";
        String deletePatientSql = "DELETE FROM patients WHERE patient_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psBills = conn.prepareStatement(deleteBillsSql)) {
                    psBills.setInt(1, patientId);
                    psBills.executeUpdate();
                }
                try (PreparedStatement psAppts = conn.prepareStatement(deleteApptsSql)) {
                    psAppts.setInt(1, patientId);
                    psAppts.executeUpdate();
                }
                int affected;
                try (PreparedStatement psPat = conn.prepareStatement(deletePatientSql)) {
                    psPat.setInt(1, patientId);
                    affected = psPat.executeUpdate();
                }
                conn.commit();
                return affected > 0;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Transaction error deleting patient id: " + patientId, e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error deleting patient id: " + patientId, e);
            return false;
        }
    }

    @Override
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, full_name, address, contact_number, email FROM patients ORDER BY full_name";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving all patients", e);
        }
        return list;
    }

    @Override
    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPatients();
        }

        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, full_name, address, contact_number, email FROM patients " +
                     "WHERE LOWER(full_name) LIKE ? OR contact_number LIKE ? OR LOWER(email) LIKE ? ORDER BY full_name";
        String pattern = "%" + query.trim().toLowerCase() + "%";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error searching patients with query: " + query, e);
        }
        return list;
    }

    @Override
    public List<PatientAppointmentHistoryItem> getPatientAppointmentHistory(int patientId) {
        List<PatientAppointmentHistoryItem> history = new ArrayList<>();
        String sql = "SELECT a.appointment_number, a.patient_id, p.full_name AS patient_name, " +
                     "       a.dentist_id, d.name AS dentist_name, d.specialization AS dentist_specialization, " +
                     "       a.treatment_id, t.treatment_name, " +
                     "       a.appointment_date, a.appointment_time, a.status AS appointment_status, " +
                     "       t.standard_fee AS treatment_cost, " +
                     "       COALESCE(b.consultation_fee, 1500.00) AS consultation_fee, " +
                     "       COALESCE(b.total_amount, (t.standard_fee + 1500.00)) AS total_amount, " +
                     "       COALESCE(b.payment_status, 'UNBILLED') AS payment_status, " +
                     "       b.issued_at AS bill_issued_at " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "LEFT JOIN bills b ON a.appointment_number = b.appointment_number " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PatientAppointmentHistoryItem item = new PatientAppointmentHistoryItem();
                    item.setAppointmentNumber(rs.getInt("appointment_number"));
                    item.setPatientId(rs.getInt("patient_id"));
                    item.setPatientName(rs.getString("patient_name"));
                    item.setDentistId(rs.getInt("dentist_id"));
                    item.setDentistName(rs.getString("dentist_name"));
                    item.setDentistSpecialization(rs.getString("dentist_specialization"));
                    item.setTreatmentId(rs.getInt("treatment_id"));
                    item.setTreatmentName(rs.getString("treatment_name"));

                    Date apptDate = rs.getDate("appointment_date");
                    if (apptDate != null) {
                        item.setAppointmentDate(apptDate.toLocalDate());
                    }

                    Time apptTime = rs.getTime("appointment_time");
                    if (apptTime != null) {
                        item.setAppointmentTime(apptTime.toLocalTime());
                    }

                    item.setAppointmentStatus(rs.getString("appointment_status"));
                    item.setTreatmentCost(rs.getDouble("treatment_cost"));
                    item.setConsultationFee(rs.getDouble("consultation_fee"));
                    item.setTotalAmount(rs.getDouble("total_amount"));
                    item.setPaymentStatus(rs.getString("payment_status"));

                    Timestamp issuedAt = rs.getTimestamp("bill_issued_at");
                    if (issuedAt != null) {
                        item.setBillIssuedAt(issuedAt.toLocalDateTime());
                    }

                    history.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving patient appointment history for patient ID: " + patientId, e);
        }
        return history;
    }

    @Override
    public Map<String, Object> getPatientAnalytics() {
        Map<String, Object> metrics = new HashMap<>();
        int totalUniquePatients = 0;
        int repeatPatients = 0;
        int newPatientsThisMonth = 0;

        String totalSql = "SELECT COUNT(*) FROM patients";
        String repeatSql = "SELECT COUNT(*) FROM (SELECT patient_id FROM appointments GROUP BY patient_id HAVING COUNT(appointment_number) > 1) AS repeat_pts";
        String newThisMonthSql = "SELECT COUNT(*) FROM (SELECT patient_id, MIN(appointment_date) AS first_date FROM appointments GROUP BY patient_id HAVING YEAR(first_date) = YEAR(CURRENT_DATE()) AND MONTH(first_date) = MONTH(CURRENT_DATE())) AS new_pts";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(totalSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalUniquePatients = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(repeatSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    repeatPatients = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(newThisMonthSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    newPatientsThisMonth = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error calculating patient retention analytics", e);
        }

        int singleVisitPatients = Math.max(0, totalUniquePatients - repeatPatients);
        double repeatRate = totalUniquePatients > 0 ? ((double) repeatPatients * 100.0 / totalUniquePatients) : 0.0;
        double retentionRatio = totalUniquePatients > 0 ? ((double) repeatPatients / totalUniquePatients) : 0.0;

        metrics.put("totalUniquePatients", totalUniquePatients);
        metrics.put("repeatPatients", repeatPatients);
        metrics.put("singleVisitPatients", singleVisitPatients);
        metrics.put("newPatientsThisMonth", newPatientsThisMonth);
        metrics.put("repeatRate", repeatRate);
        metrics.put("retentionRatio", retentionRatio);

        return metrics;
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFullName(rs.getString("full_name"));
        patient.setAddress(rs.getString("address"));
        patient.setContactNumber(rs.getString("contact_number"));
        try {
            patient.setEmail(rs.getString("email"));
        } catch (SQLException ignored) {
            // Column may be missing in legacy result sets
        }
        return patient;
    }
}
