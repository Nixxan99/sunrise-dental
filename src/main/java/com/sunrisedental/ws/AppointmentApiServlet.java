package com.sunrisedental.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.impl.AppointmentDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Distributed RESTful Web Service Endpoint for Appointment Management (Task B Requirement).
 * Responds with JSON for integration with external clients and distributed hospital systems.
 */
@WebServlet(name = "AppointmentApiServlet", urlPatterns = {"/api/appointments", "/api/appointments/*"})
public class AppointmentApiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AppointmentApiServlet.class.getName());

    private AppointmentDAO appointmentDAO;
    private Gson gson;

    @Override
    public void init() {
        this.appointmentDAO = new AppointmentDAOImpl();
        this.gson = createGson();
    }

    // Setter for testing / DI
    public void setAppointmentDAO(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> {
                    String timeStr = json.getAsString();
                    if (timeStr.length() == 5) timeStr += ":00";
                    return LocalTime.parse(timeStr);
                })
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .setPrettyPrinting()
                .create();
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        response.setContentType("application/json; charset=UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.trim().isEmpty()) {
            // Return all appointments
            List<Appointment> list = appointmentDAO.getAllAppointments();
            JsonObject result = new JsonObject();
            result.addProperty("status", "success");
            result.addProperty("count", list.size());
            result.add("data", gson.toJsonTree(list));
            out.print(gson.toJson(result));
            return;
        }

        // Parse appointment ID from path: /api/appointments/{id}
        String idStr = pathInfo.substring(1).trim();
        try {
            int apptNumber = Integer.parseInt(idStr);
            Appointment appt = appointmentDAO.getAppointmentByNumber(apptNumber);

            if (appt != null) {
                JsonObject result = new JsonObject();
                result.addProperty("status", "success");
                result.add("data", gson.toJsonTree(appt));
                out.print(gson.toJson(result));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "Appointment not found with ID: " + apptNumber);
                out.print(gson.toJson(error));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Invalid appointment ID format: " + idStr);
            out.print(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder jsonPayload = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonPayload.append(line);
            }
        }

        if (jsonPayload.length() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Empty request body");
            out.print(gson.toJson(error));
            return;
        }

        try {
            Appointment appt = gson.fromJson(jsonPayload.toString(), Appointment.class);

            if (appt.getPatientId() <= 0 || appt.getDentistId() <= 0 || appt.getTreatmentId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "patientId, dentistId, and treatmentId are required positive integers");
                out.print(gson.toJson(error));
                return;
            }

            if (appt.getAppointmentDate() != null && !ValidationUtil.isValidAppointmentDate(appt.getAppointmentDate())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "Appointment date cannot be in the past");
                out.print(gson.toJson(error));
                return;
            }

            if (appt.getStatus() == null || appt.getStatus().trim().isEmpty()) {
                appt.setStatus("SCHEDULED");
            }

            boolean created = appointmentDAO.registerAppointment(appt);
            if (created) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                Appointment savedAppt = appointmentDAO.getAppointmentByNumber(appt.getAppointmentNumber());
                JsonObject result = new JsonObject();
                result.addProperty("status", "created");
                result.addProperty("message", "Appointment registered successfully");
                result.add("data", gson.toJsonTree(savedAppt != null ? savedAppt : appt));
                out.print(gson.toJson(result));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "Database insertion failed");
                out.print(gson.toJson(error));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing API POST request", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Invalid JSON payload: " + e.getMessage());
            out.print(gson.toJson(error));
        }
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
    }
}
