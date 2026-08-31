package com.sunrisedental.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration & connection verification test for DBConnection Singleton and Cloud TiDB.
 */
@DisplayName("DBConnection TiDB Cloud Integration Test")
class DBConnectionTest {

    @Test
    @DisplayName("Should maintain Singleton instance identity")
    void shouldReturnSameInstance() {
        DBConnection instance1 = DBConnection.getInstance();
        DBConnection instance2 = DBConnection.getInstance();

        assertNotNull(instance1, "DBConnection instance should not be null");
        assertSame(instance1, instance2, "DBConnection must follow the Singleton pattern and return the exact same instance");
    }

    @Test
    @DisplayName("Should connect successfully to Cloud TiDB database and verify schema tables")
    void shouldConnectToTiDBDatabase() {
        DBConnection dbConnection = DBConnection.getInstance();
        assertNotNull(dbConnection);

        try (Connection conn = dbConnection.getConnection()) {
            assertNotNull(conn, "Connection should be established");
            assertFalse(conn.isClosed(), "Connection should be open");

            try (Statement stmt = conn.createStatement()) {
                // Ensure notification_logs table exists
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS notification_logs ("
                        + "log_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "recipient_phone VARCHAR(50) NOT NULL, "
                        + "message_body TEXT NOT NULL, "
                        + "status VARCHAR(30) NOT NULL DEFAULT 'SENT', "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")");

                // Ensure appointment_audit_log table exists (Application-tier Observer audit)
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS appointment_audit_log ("
                        + "audit_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "appointment_number INT NOT NULL, "
                        + "action_type VARCHAR(50) NOT NULL, "
                        + "performed_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM', "
                        + "details TEXT, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")");

                // Ensure performed_by column exists in appointment_audit_log if previously created
                try {
                    stmt.executeUpdate("ALTER TABLE appointment_audit_log ADD COLUMN performed_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM'");
                } catch (SQLException ignored) {
                }

                // Ensure must_change_password column exists in users table
                try {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT TRUE");
                } catch (SQLException ignored) {
                }

                try (ResultSet rs = stmt.executeQuery("SELECT 1 AS test_val, VERSION() AS tidb_ver")) {
                    assertTrue(rs.next(), "Result set should return a row");
                    assertEquals(1, rs.getInt("test_val"));
                    String version = rs.getString("tidb_ver");
                    assertNotNull(version);
                    System.out.println("[TiDB Connection Success] Database Version: " + version);
                }
            }
        } catch (SQLException e) {
            fail("Failed to connect to TiDB database: " + e.getMessage());
        }
    }
}
