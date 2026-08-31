package com.sunrisedental.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database connection manager for the Sunrise Dental Clinic System (CIS6003).
 * Implements thread-safe lazy initialization using the double-checked locking pattern.
 * Configured for Cloud TiDB MySQL-compatible database.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static final String DEFAULT_URL =
            "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/sunrise_dental_db?sslMode=VERIFY_IDENTITY&useSSL=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "42UVtMmfzWtuANk.root";
    private static final String DEFAULT_PASSWORD = "xn0oZNxqPx9YNeU3";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // volatile keyword ensures atomic read/write visibility across threads
    private static volatile DBConnection instance;

    private String url;
    private String username;
    private String password;

    /**
     * Private constructor prevents direct instantiation from other classes.
     * Loads MySQL driver and database configuration properties.
     */
    private DBConnection() {
        loadConfiguration();
        loadDriver();
    }

    /**
     * Thread-safe lazy initialization using double-checked locking.
     *
     * @return the singleton instance of DBConnection
     */
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Loads database configuration settings from db.properties on classpath,
     * environment variables, or system properties, falling back to default TiDB settings.
     */
    private void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                properties.load(in);
                LOGGER.info("Successfully loaded database configuration from db.properties");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not load db.properties file, falling back to default settings", e);
        }

        this.url = System.getProperty("db.url",
                System.getenv().getOrDefault("DB_URL",
                        properties.getProperty("db.url", DEFAULT_URL)));

        this.username = System.getProperty("db.user",
                System.getenv().getOrDefault("DB_USER",
                        properties.getProperty("db.user", DEFAULT_USER)));

        this.password = System.getProperty("db.password",
                System.getenv().getOrDefault("DB_PASSWORD",
                        properties.getProperty("db.password", DEFAULT_PASSWORD)));
    }

    /**
     * Loads and registers the MySQL JDBC Driver.
     */
    private void loadDriver() {
        try {
            Class.forName(DRIVER_CLASS);
            LOGGER.info("MySQL JDBC Driver registered successfully: " + DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to locate MySQL JDBC Driver: " + DRIVER_CLASS, e);
            throw new RuntimeException("MySQL JDBC Driver not found in classpath: " + DRIVER_CLASS, e);
        }
    }

    /**
     * Obtains a new database connection from the driver manager.
     *
     * @return a valid java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    /**
     * Verifies that the database connection can be established.
     *
     * @return true if successfully connected, false otherwise
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Utility method to safely close JDBC resources.
     *
     * @param conn the Connection to close
     * @param stmt the Statement to close
     * @param rs the ResultSet to close
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Error closing ResultSet", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Error closing Statement", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Error closing Connection", e);
            }
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    public static void close(Connection conn) {
        close(conn, null, null);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
}
