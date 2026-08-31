package com.sunrisedental.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized configuration utility for managing application properties.
 * Loads properties from classpath (config.properties or db.properties) with
 * priority fallback to System Environment Variables and JVM System Properties.
 */
public class ConfigUtil {

    private static final Logger LOGGER = Logger.getLogger(ConfigUtil.class.getName());
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private ConfigUtil() {
        // Utility class
    }

    private static void loadProperties() {
        // Attempt 1: Load config.properties
        try (InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                PROPERTIES.load(is);
                LOGGER.info("Loaded application configuration from config.properties");
                return;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load config.properties: " + e.getMessage(), e);
        }

        // Attempt 2: Fallback to db.properties
        try (InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                PROPERTIES.load(is);
                LOGGER.info("Loaded configuration fallback from db.properties");
            } else {
                LOGGER.info("No configuration file found on classpath; relying solely on environment variables and defaults.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load db.properties fallback: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the configuration value for a specified key.
     * Checks Environment Variables first, then System Properties, then config.properties.
     *
     * @param key the property key (e.g., "db.url", "gemini.api.key", "mail.sender")
     * @return the property value, or null if not found
     */
    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Retrieves the configuration value for a specified key with a default fallback.
     *
     * @param key the property key
     * @param defaultValue default value if key is not found or empty
     * @return the property value, or defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        if (key == null || key.trim().isEmpty()) {
            return defaultValue;
        }

        // 1. Check System Environment Variable (exact match and uppercase underscore convention)
        String envKey = key.toUpperCase().replace('.', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }

        String rawEnvVal = System.getenv(key);
        if (rawEnvVal != null && !rawEnvVal.trim().isEmpty()) {
            return rawEnvVal.trim();
        }

        // Special environment variable aliases
        if ("mail.sender".equalsIgnoreCase(key) || "mail.user".equalsIgnoreCase(key)) {
            String clinicEmail = System.getenv("CLINIC_EMAIL");
            if (clinicEmail != null && !clinicEmail.trim().isEmpty()) {
                return clinicEmail.trim();
            }
        }
        if ("mail.password".equalsIgnoreCase(key)) {
            String clinicPass = System.getenv("CLINIC_EMAIL_PASSWORD");
            if (clinicPass != null && !clinicPass.trim().isEmpty()) {
                return clinicPass.trim();
            }
        }

        // 2. Check JVM System Property
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        // 3. Check loaded config.properties file
        String fileVal = PROPERTIES.getProperty(key);
        if (fileVal != null && !fileVal.trim().isEmpty()) {
            return fileVal.trim();
        }

        return defaultValue;
    }
}
