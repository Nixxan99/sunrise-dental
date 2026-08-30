package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Unit Tests for BillingService (Task C Requirement).
 */
@DisplayName("BillingService Tests")
class BillingServiceTest {

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService();
    }

    @Nested
    @DisplayName("Total Cost Calculation (Treatment Cost + Consultation Fee)")
    class TotalCostCalculationTests {

        @Test
        @DisplayName("Should accurately compute standard consultation and treatment total")
        void shouldCalculateTotalCostCorrectly() {
            double consultationFee = 1500.00;
            double treatmentCost = 4500.00;
            double expectedTotal = 6000.00;

            double actualTotal = billingService.calculateTotalCost(treatmentCost, consultationFee);

            assertEquals(expectedTotal, actualTotal, 0.001,
                    "Total should be exactly the sum of treatment cost and consultation fee");
        }

        @Test
        @DisplayName("Should correctly compute when one fee is zero")
        void shouldHandleZeroFee() {
            assertEquals(2000.00, billingService.calculateTotalCost(2000.00, 0.00), 0.001);
            assertEquals(1200.00, billingService.calculateTotalCost(0.00, 1200.00), 0.001);
            assertEquals(0.00, billingService.calculateTotalCost(0.00, 0.00), 0.001);
        }

        @ParameterizedTest(name = "Treatment: {0}, Consultation: {1}, Expected: {2}")
        @CsvSource({
                "100.50, 50.25, 150.75",
                "2500.00, 1000.00, 3500.00",
                "15000.75, 2500.25, 17501.00",
                "99.99, 0.01, 100.00"
        })
        void shouldCalculateVariousFeeCombinations(double treatmentCost, double consultationFee, double expected) {
            assertEquals(expected, billingService.calculateTotalCost(treatmentCost, consultationFee), 0.001);
        }
    }

    @Nested
    @DisplayName("Validation Rejecting Negative Fees")
    class NegativeFeeValidationTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when treatment cost is negative")
        void shouldRejectNegativeTreatmentCost() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    billingService.calculateTotalCost(-500.00, 1500.00));
            assertTrue(exception.getMessage().toLowerCase().contains("negative"),
                    "Exception message should mention negative value");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when consultation fee is negative")
        void shouldRejectNegativeConsultationFee() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    billingService.calculateTotalCost(3000.00, -200.00));
            assertTrue(exception.getMessage().toLowerCase().contains("negative"),
                    "Exception message should mention negative value");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when both fees are negative")
        void shouldRejectBothNegativeFees() {
            assertThrows(IllegalArgumentException.class, () ->
                    billingService.calculateTotalCost(-1000.00, -500.00));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException in createBill for negative fees")
        void shouldRejectNegativeFeesWhenCreatingBill() {
            assertThrows(IllegalArgumentException.class, () ->
                    billingService.createBill(101, -100.00, 2000.00, "PAID"));

            assertThrows(IllegalArgumentException.class, () ->
                    billingService.createBill(101, 1500.00, -50.00, "PAID"));
        }
    }

    @Nested
    @DisplayName("Receipt Generation Logic")
    class ReceiptGenerationTests {

        @Test
        @DisplayName("Should generate complete formatted receipt with appointment and bill details")
        void shouldGenerateDetailedReceiptWithAppointment() {
            Bill bill = new Bill(1, 101, 1500.00, 5000.00, 6500.00, "PAID",
                    LocalDateTime.of(2026, 8, 30, 14, 30));

            Appointment appt = new Appointment(101, 1, 2, 3,
                    LocalDate.of(2026, 8, 30), LocalTime.of(14, 0), "COMPLETED",
                    "John Doe", "Dr. Sarah Smith", "Root Canal Therapy", 5000.00);

            String receipt = billingService.generateReceipt(bill, appt);

            assertNotNull(receipt, "Receipt must not be null");
            assertTrue(receipt.contains("SUNRISE DENTAL CLINIC"), "Receipt should contain clinic header");
            assertTrue(receipt.contains("101"), "Receipt should contain appointment number");
            assertTrue(receipt.contains("John Doe"), "Receipt should contain patient name");
            assertTrue(receipt.contains("Dr. Sarah Smith"), "Receipt should contain doctor name");
            assertTrue(receipt.contains("Root Canal Therapy"), "Receipt should contain treatment name");
            assertTrue(receipt.contains("1500.00") || receipt.contains("1,500.00"),
                    "Receipt should contain consultation fee");
            assertTrue(receipt.contains("5000.00") || receipt.contains("5,000.00"),
                    "Receipt should contain treatment cost");
            assertTrue(receipt.contains("6500.00") || receipt.contains("6,500.00"),
                    "Receipt should contain total amount");
            assertTrue(receipt.contains("PAID"), "Receipt should contain payment status");
        }

        @Test
        @DisplayName("Should generate receipt even when appointment presentation object is null")
        void shouldGenerateReceiptWithoutAppointment() {
            Bill bill = new Bill(2, 202, 1000.00, 3000.00, 4000.00, "PENDING", LocalDateTime.now());

            String receipt = billingService.generateReceipt(bill);

            assertNotNull(receipt);
            assertTrue(receipt.contains("SUNRISE DENTAL CLINIC"));
            assertTrue(receipt.contains("202"));
            assertTrue(receipt.contains("4000.00") || receipt.contains("4,000.00"));
            assertTrue(receipt.contains("PENDING"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when bill is null")
        void shouldRejectNullBillForReceiptGeneration() {
            assertThrows(IllegalArgumentException.class, () ->
                    billingService.generateReceipt(null));
        }
    }
}
