package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.NotificationLogDAO;
import com.sunrisedental.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of NotificationLogDAO using PreparedStatement to record audit logs.
 */
public class NotificationLogDAOImpl implements NotificationLogDAO {

    private static final Logger LOGGER = Logger.getLogger(NotificationLogDAOImpl.class.getName());

    @Override
    public boolean logNotification(String recipientPhone, String messageBody, String status) {
        String sql = "INSERT INTO notification_logs (recipient_phone, message_body, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recipientPhone != null ? recipientPhone : "UNKNOWN");
            ps.setString(2, messageBody != null ? messageBody : "");
            ps.setString(3, status != null ? status : "SENT");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error saving notification log: " + e.getMessage(), e);
            return false;
        }
    }
}
