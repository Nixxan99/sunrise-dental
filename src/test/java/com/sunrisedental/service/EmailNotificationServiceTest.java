package com.sunrisedental.service;

import com.sunrisedental.dao.NotificationLogDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying asynchronous, non-blocking Gmail SMTP email delivery
 * and simulated fallback auditing.
 */
@DisplayName("EmailNotificationService Asynchronous Gmail Delivery Tests")
class EmailNotificationServiceTest {

    private List<String> capturedLogs;
    private EmailNotificationService emailService;

    @BeforeEach
    void setUp() {
        capturedLogs = new ArrayList<>();
        NotificationLogDAO mockLogDAO = (recipientPhone, messageBody, status) -> {
            capturedLogs.add(recipientPhone + ":" + status + ":" + messageBody);
            return true;
        };

        // Instantiate with null credentials to trigger graceful simulated delivery and auditing
        emailService = new EmailNotificationService(null, null, mockLogDAO);
    }

    @Test
    @DisplayName("Should deliver simulated email and log to audit table when credentials missing")
    void shouldDeliverSimulatedEmailAndAudit() {
        boolean result = emailService.sendAppointmentConfirmation(
                "patient@example.com",
                "Sunil Perera",
                "Dr. Kasun Silva",
                "Root Canal Therapy",
                "2026-09-05",
                "10:30",
                701
        );

        assertTrue(result, "Email dispatch should return true");
        assertEquals(1, capturedLogs.size(), "Should record 1 log entry in notification_logs");
        String log = capturedLogs.get(0);
        assertTrue(log.startsWith("patient@example.com:SENT:"));
        assertTrue(log.contains("Appointment Number: #701"));
        assertTrue(log.contains("Sunil Perera"));
        assertTrue(log.contains("Dr. Kasun Silva"));
    }

    @Test
    @DisplayName("Should execute asynchronously via CompletableFuture without throwing exceptions")
    void shouldExecuteAsynchronously() throws Exception {
        CompletableFuture<Boolean> future = emailService.sendAppointmentConfirmationAsync(
                "recipient@test.com",
                "Kamal",
                "Dr. Perera",
                "Teeth Cleaning",
                "2026-09-10",
                "14:00",
                702
        );

        assertNotNull(future);
        Boolean res = future.get(5, TimeUnit.SECONDS);
        assertTrue(res);
        assertTrue(capturedLogs.stream().anyMatch(l -> l.contains("702")));
    }

    @Test
    @DisplayName("Should safely skip delivery if recipient email is null or empty")
    void shouldSkipIfRecipientNullOrEmpty() {
        boolean res1 = emailService.sendAppointmentConfirmation(null, "Test", "Dr", "Tx", "2026-09-01", "10:00", 1);
        boolean res2 = emailService.sendAppointmentConfirmation("   ", "Test", "Dr", "Tx", "2026-09-01", "10:00", 2);

        assertFalse(res1);
        assertFalse(res2);
        assertTrue(capturedLogs.isEmpty());
    }
}
