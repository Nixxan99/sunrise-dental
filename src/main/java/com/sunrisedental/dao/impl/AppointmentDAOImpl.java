package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.service.AuditService;
import com.sunrisedental.service.observer.AppointmentRegistrationListener;
import com.sunrisedental.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object implementation for Appointment operations.
 * Uses PreparedStatement to prevent SQL injection, joins related entities for presentation,
 * and notifies registered application-tier Observers (such as AuditService) upon state changes.
 */
public class AppointmentDAOImpl implements AppointmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAOImpl.class.getName());

    // Observer Pattern: Application-tier event listeners replacing native database triggers
    private static final List<AppointmentRegistrationListener> REGISTRATION_LISTENERS =
            new CopyOnWriteArrayList<>();

    static {
        // Register default application AuditService as an Observer
        REGISTRATION_LISTENERS.add(new AuditService());
    }

    public static void addRegistrationListener(AppointmentRegistrationListener listener) {
        if (listener != null) {
            REGISTRATION_LISTENERS.add(listener);
        }
    }

    public static void removeRegistrationListener(AppointmentRegistrationListener listener) {
        if (listener != null) {
            REGISTRATION_LISTENERS.remove(listener);
        }
    }

    private static final String SELECT_JOINED_APPOINTMENT =
            "SELECT a.appointment_number, a.patient_id, a.dentist_id, a.treatment_id, " +
            "       a.appointment_date, a.appointment_time, a.status, " +
            "       p.full_name AS patient_name, " +
            "       d.name AS dentist_name, " +
            "       t.treatment_name, " +
            "       t.standard_fee AS cost " +
            "FROM appointments a " +
            "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
            "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
            "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id ";

    @Override
    public boolean registerAppointment(Appointment appt) {
        if (appt == null) {
            return false;
        }

        String sql = "INSERT INTO appointments (patient_id, dentist_id, treatment_id, " +
                     "appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appt.getPatientId());
            ps.setInt(2, appt.getDentistId());
            ps.setInt(3, appt.getTreatmentId());

            if (appt.getAppointmentDate() != null) {
                ps.setDate(4, Date.valueOf(appt.getAppointmentDate()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            if (appt.getAppointmentTime() != null) {
                ps.setTime(5, Time.valueOf(appt.getAppointmentTime()));
            } else {
                ps.setNull(5, java.sql.Types.TIME);
            }

            ps.setString(6, appt.getStatus() != null ? appt.getStatus() : "SCHEDULED");

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appt.setAppointmentNumber(generatedKeys.getInt(1));
                    }
                }

                // Notify Observers (e.g. AuditService) in the application tier
                notifyRegistrationListeners(appt, "SYSTEM");

                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error registering appointment", e);
        }
        return false;
    }

    private void notifyRegistrationListeners(Appointment appt, String performedBy) {
        for (AppointmentRegistrationListener listener : REGISTRATION_LISTENERS) {
            try {
                listener.onAppointmentRegistered(appt, performedBy);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error notifying AppointmentRegistrationListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public Appointment getAppointmentByNumber(String apptNumber) {
        if (apptNumber == null || apptNumber.trim().isEmpty()) {
            return null;
        }
        try {
            int number = Integer.parseInt(apptNumber.replaceAll("\\D", ""));
            return getAppointmentByNumber(number);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid appointment number format: " + apptNumber, e);
            return null;
        }
    }

    @Override
    public Appointment getAppointmentByNumber(int apptNumber) {
        String sql = SELECT_JOINED_APPOINTMENT + "WHERE a.appointment_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, apptNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapJoinedResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving appointment by number: " + apptNumber, e);
        }
        return null;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_JOINED_APPOINTMENT + "ORDER BY a.appointment_date DESC, a.appointment_time ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapJoinedResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving all appointments", e);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int appointmentNumber, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, appointmentNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error updating status for appointment: " + appointmentNumber, e);
        }
        return false;
    }

    private Appointment mapJoinedResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appt = new Appointment();
        appt.setAppointmentNumber(rs.getInt("appointment_number"));
        appt.setPatientId(rs.getInt("patient_id"));
        appt.setDentistId(rs.getInt("dentist_id"));
        appt.setTreatmentId(rs.getInt("treatment_id"));

        Date sqlDate = rs.getDate("appointment_date");
        if (sqlDate != null) {
            appt.setAppointmentDate(sqlDate.toLocalDate());
        }

        Time sqlTime = rs.getTime("appointment_time");
        if (sqlTime != null) {
            appt.setAppointmentTime(sqlTime.toLocalTime());
        }

        appt.setStatus(rs.getString("status"));

        // Joined presentation fields
        appt.setPatientName(rs.getString("patient_name"));
        appt.setDentistName(rs.getString("dentist_name"));
        appt.setTreatmentName(rs.getString("treatment_name"));
        appt.setCost(rs.getDouble("cost"));

        return appt;
    }
}
