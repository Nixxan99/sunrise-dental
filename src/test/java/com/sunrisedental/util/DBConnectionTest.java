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
    @DisplayName("Should connect successfully to Cloud TiDB database")
    void shouldConnectToTiDBDatabase() {
        DBConnection dbConnection = DBConnection.getInstance();
        assertNotNull(dbConnection);

        try (Connection conn = dbConnection.getConnection()) {
            assertNotNull(conn, "Connection should be established");
            assertFalse(conn.isClosed(), "Connection should be open");

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1 AS test_val, VERSION() AS tidb_ver")) {
                assertTrue(rs.next(), "Result set should return a row");
                assertEquals(1, rs.getInt("test_val"));
                String version = rs.getString("tidb_ver");
                assertNotNull(version);
                System.out.println("[TiDB Connection Success] Database Version: " + version);
            }
        } catch (SQLException e) {
            fail("Failed to connect to TiDB database: " + e.getMessage());
        }
    }
}
