package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.AppointmentAuditDAO;
import com.sunrisedental.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of AppointmentAuditDAO using PreparedStatement to log audit records.
 * Resilient to schema variations across distributed databases.
 */
public class AppointmentAuditDAOImpl implements AppointmentAuditDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentAuditDAOImpl.class.getName());

    @Override
    public boolean logAppointmentAudit(int appointmentNumber, String actionType, String performedBy, String details) {
        String safeAction = actionType != null ? actionType : "APPOINTMENT_REGISTERED";
        String safeUser = performedBy != null ? performedBy : "SYSTEM";
        String safeDetails = details != null ? details : "";

        String primarySql = "INSERT INTO appointment_audit_log (appointment_number, action_type, performed_by, details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(primarySql)) {
                ps.setInt(1, appointmentNumber);
                ps.setString(2, safeAction);
                ps.setString(3, safeUser);
                ps.setString(4, safeDetails);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                // If column performed_by does not exist, try auto-migrating or fallback
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("performed_by")) {
                    LOGGER.info("Column 'performed_by' missing in appointment_audit_log. Attempting schema migration and fallback insert.");
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.executeUpdate("ALTER TABLE appointment_audit_log ADD COLUMN performed_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM'");
                        // Retry with primarySql
                        try (PreparedStatement retryPs = conn.prepareStatement(primarySql)) {
                            retryPs.setInt(1, appointmentNumber);
                            retryPs.setString(2, safeAction);
                            retryPs.setString(3, safeUser);
                            retryPs.setString(4, safeDetails);
                            return retryPs.executeUpdate() > 0;
                        }
                    } catch (SQLException alterEx) {
                        // Fallback without performed_by column
                        String fallbackSql = "INSERT INTO appointment_audit_log (appointment_number, action_type, details) VALUES (?, ?, ?)";
                        try (PreparedStatement fallbackPs = conn.prepareStatement(fallbackSql)) {
                            fallbackPs.setInt(1, appointmentNumber);
                            fallbackPs.setString(2, safeAction);
                            fallbackPs.setString(3, "[" + safeUser + "] " + safeDetails);
                            return fallbackPs.executeUpdate() > 0;
                        }
                    }
                }
                LOGGER.log(Level.WARNING, "Failed to log appointment audit: " + e.getMessage(), e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection error in logAppointmentAudit: " + e.getMessage(), e);
            return false;
        }
    }
}
