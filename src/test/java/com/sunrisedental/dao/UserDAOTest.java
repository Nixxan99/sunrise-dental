package com.sunrisedental.dao;

import com.sunrisedental.dao.impl.UserDAOImpl;
import com.sunrisedental.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User security logic, password hashing, and User POJO behavior.
 */
@DisplayName("User Security & DAO Password Verification Tests")
class UserDAOTest {

    @Nested
    @DisplayName("Password Hashing and Verification Logic")
    class HashingTests {

        @Test
        @DisplayName("Should correctly generate 64-character hex SHA-256 hash")
        void shouldGenerateValidSha256Hash() {
            String hash = UserDAOImpl.hashPassword("admin123");
            assertNotNull(hash);
            assertEquals(64, hash.length(), "SHA-256 hash in hex should be 64 characters long");
            assertEquals("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", hash);
        }

        @Test
        @DisplayName("Should successfully verify plain password against its SHA-256 hash")
        void shouldVerifyMatchingPasswordHash() {
            String plain = "SecretPassword!2026";
            String hash = UserDAOImpl.hashPassword(plain);

            assertTrue(UserDAOImpl.verifyPassword(plain, hash));
        }

        @Test
        @DisplayName("Should fail verification when password does not match hash")
        void shouldRejectMismatchedPassword() {
            String hash = UserDAOImpl.hashPassword("correctPassword");
            assertFalse(UserDAOImpl.verifyPassword("wrongPassword", hash));
        }

        @Test
        @DisplayName("Should handle null inputs safely in password verification")
        void shouldHandleNullInputsInVerifyPassword() {
            assertFalse(UserDAOImpl.verifyPassword(null, "someHash"));
            assertFalse(UserDAOImpl.verifyPassword("plain", null));
            assertFalse(UserDAOImpl.verifyPassword(null, null));
        }
    }

    @Nested
    @DisplayName("User Model Domain Tests")
    class UserModelTests {

        @Test
        @DisplayName("Should properly instantiate User with mustChangePassword flag")
        void shouldInstantiateUserWithMustChangePassword() {
            User user = new User("nayomi", "hash123", "Nayomi Silva", "STAFF", true);
            assertEquals("nayomi", user.getUsername());
            assertEquals("hash123", user.getPasswordHash());
            assertEquals("Nayomi Silva", user.getFullName());
            assertEquals("STAFF", user.getRole());
            assertTrue(user.isMustChangePassword());

            user.setMustChangePassword(false);
            assertFalse(user.isMustChangePassword());
        }

        @Test
        @DisplayName("Should verify equals and hashCode contracts")
        void shouldVerifyEqualsAndHashCode() {
            User u1 = new User(1, "admin", "hash", "Admin User", "ADMIN", false);
            User u2 = new User(1, "admin", "hash", "Admin User", "ADMIN", false);
            User u3 = new User(2, "receptionist", "hash2", "Receptionist", "RECEPTIONIST", true);

            assertEquals(u1, u2);
            assertEquals(u1.hashCode(), u2.hashCode());
            assertNotEquals(u1, u3);
        }
    }
}
