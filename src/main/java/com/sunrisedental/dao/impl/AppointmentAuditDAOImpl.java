package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.AppointmentAuditDAO;
import com.sunrisedental.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of AppointmentAuditDAO using PreparedStatement to log audit records.
 */
public class AppointmentAuditDAOImpl implements AppointmentAuditDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentAuditDAOImpl.class.getName());

    @Override
    public boolean logAppointmentAudit(int appointmentNumber, String actionType, String performedBy, String details) {
        String sql = "INSERT INTO appointment_audit_log (appointment_number, action_type, performed_by, details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentNumber);
            ps.setString(2, actionType != null ? actionType : "APPOINTMENT_REGISTERED");
            ps.setString(3, performedBy != null ? performedBy : "SYSTEM");
            ps.setString(4, details != null ? details : "");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to log appointment audit: " + e.getMessage(), e);
            return false;
        }
    }
}
