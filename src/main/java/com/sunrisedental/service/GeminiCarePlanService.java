package com.sunrisedental.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunrisedental.util.ConfigUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service integrating Google Gemini Generative AI (gemini-1.5-flash) to generate
 * personalized patient post-treatment recovery and care plans (CIS6003 Task B).
 * Configuration is dynamically resolved via ConfigUtil with resilient offline fallback.
 */
public class GeminiCarePlanService {

    private static final Logger LOGGER = Logger.getLogger(GeminiCarePlanService.class.getName());
    private static final String GEMINI_ENDPOINT_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final String apiKey;
    private final Gson gson;

    public GeminiCarePlanService() {
        String key = ConfigUtil.getProperty("gemini.api.key");
        this.apiKey = (key != null && !key.trim().isEmpty()) ? key.trim() : null;
        this.gson = new Gson();
    }

    public GeminiCarePlanService(String apiKey) {
        this.apiKey = (apiKey != null && !apiKey.trim().isEmpty()) ? apiKey.trim() : null;
        this.gson = new Gson();
    }

    /**
     * Generates personalized post-treatment care tips using Google Gemini AI,
     * or returns clinical fallback advice if offline/unconfigured.
     *
     * @param treatmentName dental procedure performed (e.g. "Root Canal Therapy", "Teeth Cleaning")
     * @param patientName patient's full name
     * @return 3 concise bulleted post-treatment care instructions
     */
    public String generatePostTreatmentAdvice(String treatmentName, String patientName) {
        String safePatient = (patientName != null && !patientName.trim().isEmpty()) ? patientName.trim() : "Patient";
        String safeTreatment = (treatmentName != null && !treatmentName.trim().isEmpty()) ? treatmentName.trim() : "Dental Treatment";

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.info("No GEMINI_API_KEY detected. Utilizing built-in clinical care plan fallback.");
            return generateFallbackAdvice(safeTreatment, safePatient);
        }

        try {
            String prompt = String.format(
                    "Provide 3 short, bulleted post-treatment recovery and care tips for a dental patient named %s who just received %s. Keep it concise, friendly, and under 60 words.",
                    safePatient, safeTreatment
            );

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);

            JsonObject contentObj = new JsonObject();
            contentObj.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(contentObj);

            JsonObject requestBodyJson = new JsonObject();
            requestBodyJson.add("contents", contentsArray);

            String requestBody = gson.toJson(requestBodyJson);

            URL url = URI.create(GEMINI_ENDPOINT_BASE + apiKey).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseSb = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseSb.append(responseLine.trim());
                    }

                    JsonObject responseJson = JsonParser.parseString(responseSb.toString()).getAsJsonObject();
                    JsonArray candidates = responseJson.getAsJsonArray("candidates");
                    if (candidates != null && candidates.size() > 0) {
                        JsonObject candidate = candidates.get(0).getAsJsonObject();
                        JsonObject content = candidate.getAsJsonObject("content");
                        if (content != null) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts != null && parts.size() > 0) {
                                String adviceText = parts.get(0).getAsJsonObject().get("text").getAsString();
                                if (adviceText != null && !adviceText.trim().isEmpty()) {
                                    return adviceText.trim();
                                }
                            }
                        }
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Gemini API returned non-200 HTTP code: " + responseCode);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Gemini AI connection failed: " + e.getMessage() + ". Defaulting to clinical fallback.");
        }

        return generateFallbackAdvice(safeTreatment, safePatient);
    }

    /**
     * Curated, clinically-aligned fallback care plans for common dental procedures.
     */
    public String generateFallbackAdvice(String treatmentName, String patientName) {
        String lower = treatmentName.toLowerCase();
        if (lower.contains("root canal")) {
            return String.format(
                    "• Dear %s, avoid chewing hard foods on the treated side until the permanent crown is placed.\n" +
                    "• Take prescribed analgesics for mild soreness and rinse gently with warm salt water.\n" +
                    "• Contact Sunrise Dental immediately if you experience prolonged swelling or persistent pain.",
                    patientName
            );
        } else if (lower.contains("extraction")) {
            return String.format(
                    "• Dear %s, bite gently on the gauze pad for 30–45 minutes to encourage blood clot formation.\n" +
                    "• Avoid drinking through a straw, smoking, or vigorous spitting for 48 hours.\n" +
                    "• Stick to soft foods (yogurt, soup) and apply a cold compress to minimize facial swelling.",
                    patientName
            );
        } else if (lower.contains("whitening") || lower.contains("bleach")) {
            return String.format(
                    "• Dear %s, avoid dark beverages (coffee, tea, red wine) and staining foods for 48 hours.\n" +
                    "• Use a sensitivity-relief toothpaste if mild thermal tooth sensitivity occurs.\n" +
                    "• Maintain gentle daily brushing and flossing to prolong whitening results.",
                    patientName
            );
        } else if (lower.contains("cleaning") || lower.contains("scaling")) {
            return String.format(
                    "• Dear %s, wait 30 minutes before eating or drinking hot/cold liquids if fluoride was applied.\n" +
                    "• Brush gently twice daily with a soft-bristled brush and floss daily along the gumline.\n" +
                    "• Mild gum sensitivity is normal and should subside within 24–48 hours.",
                    patientName
            );
        } else {
            return String.format(
                    "• Dear %s, rest and avoid eating until local anesthesia numbness has completely worn off.\n" +
                    "• Maintain excellent oral hygiene with gentle brushing around the treated dental area.\n" +
                    "• Stay hydrated, avoid extreme hot/cold foods, and call our clinic for any concerns.",
                    patientName
            );
        }
    }
}
