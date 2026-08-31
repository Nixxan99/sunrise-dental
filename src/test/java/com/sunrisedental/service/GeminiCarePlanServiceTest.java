package com.sunrisedental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Unit Test Suite for GeminiCarePlanService verifying Gemini AI integration
 * and reliable offline clinical fallback capabilities (CIS6003 Task B).
 */
@DisplayName("GeminiCarePlanService AI Advice & Offline Fallback Tests")
class GeminiCarePlanServiceTest {

    private GeminiCarePlanService offlineService;

    @BeforeEach
    void setUp() {
        // Explicitly instantiate with null API key to test offline / fallback resilience
        offlineService = new GeminiCarePlanService(null);
    }

    @Nested
    @DisplayName("Offline & Fallback Recovery Advice Generation Tests")
    class OfflineFallbackTests {

        @Test
        @DisplayName("Should generate clinical recovery advice for Root Canal Therapy")
        void shouldGenerateAdviceForRootCanal() {
            String advice = offlineService.generatePostTreatmentAdvice("Root Canal Therapy", "Kamal Perera");

            assertNotNull(advice, "Advice text must not be null");
            assertTrue(advice.contains("Kamal Perera"), "Advice should address the patient by name");
            assertTrue(advice.contains("•"), "Advice should contain bullet points");
            assertTrue(advice.toLowerCase().contains("chewing") || advice.toLowerCase().contains("crown"),
                    "Advice should contain root canal specific recovery guidance");
        }

        @Test
        @DisplayName("Should generate clinical recovery advice for Tooth Extraction")
        void shouldGenerateAdviceForExtraction() {
            String advice = offlineService.generatePostTreatmentAdvice("Tooth Extraction", "Nayomi Silva");

            assertNotNull(advice);
            assertTrue(advice.contains("Nayomi Silva"));
            assertTrue(advice.contains("•"));
            assertTrue(advice.toLowerCase().contains("gauze") || advice.toLowerCase().contains("straw"),
                    "Advice should contain extraction specific recovery guidance");
        }

        @Test
        @DisplayName("Should generate clinical recovery advice for Teeth Cleaning")
        void shouldGenerateAdviceForCleaning() {
            String advice = offlineService.generatePostTreatmentAdvice("Teeth Cleaning & Scaling", "Sunil Fernando");

            assertNotNull(advice);
            assertTrue(advice.contains("Sunil Fernando"));
            assertTrue(advice.contains("•"));
            assertTrue(advice.toLowerCase().contains("brush") || advice.toLowerCase().contains("sensitivity"),
                    "Advice should contain cleaning specific guidance");
        }

        @Test
        @DisplayName("Should generate clinical recovery advice for Teeth Whitening")
        void shouldGenerateAdviceForWhitening() {
            String advice = offlineService.generatePostTreatmentAdvice("Teeth Whitening", "Anoma Jayasinghe");

            assertNotNull(advice);
            assertTrue(advice.contains("Anoma Jayasinghe"));
            assertTrue(advice.contains("•"));
            assertTrue(advice.toLowerCase().contains("staining") || advice.toLowerCase().contains("beverages") || advice.toLowerCase().contains("sensitivity"),
                    "Advice should contain whitening specific guidance");
        }

        @Test
        @DisplayName("Should handle generic dental treatments gracefully")
        void shouldHandleGenericTreatments() {
            String advice = offlineService.generatePostTreatmentAdvice("Dental Checkup", "Rohan");

            assertNotNull(advice);
            assertTrue(advice.contains("Rohan"));
            assertTrue(advice.contains("•"));
            assertTrue(advice.toLowerCase().contains("hygiene") || advice.toLowerCase().contains("anesthesia"));
        }

        @Test
        @DisplayName("Should handle null and empty patient or treatment inputs safely")
        void shouldHandleNullOrEmptyInputs() {
            String advice1 = offlineService.generatePostTreatmentAdvice(null, null);
            assertNotNull(advice1);
            assertTrue(advice1.contains("•"));

            String advice2 = offlineService.generatePostTreatmentAdvice("", "   ");
            assertNotNull(advice2);
            assertTrue(advice2.contains("•"));
        }
    }

    @Nested
    @DisplayName("Direct Fallback Method Verification")
    class DirectFallbackTests {

        @Test
        @DisplayName("Direct generateFallbackAdvice call should return formatted bullet points")
        void shouldReturnDirectFallback() {
            String advice = offlineService.generateFallbackAdvice("Root Canal", "Ruwan");
            assertNotNull(advice);
            assertTrue(advice.contains("Ruwan"));
            assertTrue(advice.contains("•"));
        }
    }
}
