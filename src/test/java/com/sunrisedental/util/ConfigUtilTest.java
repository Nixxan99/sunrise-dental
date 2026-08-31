package com.sunrisedental.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigUtil Property Loading & Environment Fallback Tests")
class ConfigUtilTest {

    @Test
    @DisplayName("Should retrieve database configuration from properties file")
    void shouldRetrieveDatabaseProperties() {
        String dbUrl = ConfigUtil.getProperty("db.url");
        assertNotNull(dbUrl, "db.url should not be null");
        assertTrue(dbUrl.contains("jdbc:mysql://"), "db.url should start with jdbc:mysql://");

        String dbUser = ConfigUtil.getProperty("db.user");
        assertNotNull(dbUser, "db.user should not be null");
    }

    @Test
    @DisplayName("Should return default value when property key is missing")
    void shouldReturnDefaultValueForMissingKey() {
        String nonExistent = ConfigUtil.getProperty("non.existent.key.xyz", "default_fallback");
        assertEquals("default_fallback", nonExistent);
    }

    @Test
    @DisplayName("Should return null when property is missing and no default provided")
    void shouldReturnNullForMissingKey() {
        String nonExistent = ConfigUtil.getProperty("completely.absent.key.123");
        assertNull(nonExistent);
    }

    @Test
    @DisplayName("Should prioritize JVM System Properties over properties file")
    void shouldPrioritizeSystemProperties() {
        System.setProperty("test.custom.key", "custom_system_value");
        try {
            String value = ConfigUtil.getProperty("test.custom.key");
            assertEquals("custom_system_value", value);
        } finally {
            System.clearProperty("test.custom.key");
        }
    }
}
