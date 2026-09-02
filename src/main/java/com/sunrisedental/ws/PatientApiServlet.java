package com.sunrisedental.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.impl.PatientDAOImpl;
import com.sunrisedental.model.Patient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 * RESTful API Endpoint for Patient Search and Autocomplete.
 * Powers client-side universal lookup dropdowns and external integrations.
 */
@WebServlet(name = "PatientApiServlet", urlPatterns = {"/api/patients", "/api/patients/*"})
public class PatientApiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PatientApiServlet.class.getName());

    private PatientDAO patientDAO;
    private Gson gson;

    @Override
    public void init() {
        this.patientDAO = new PatientDAOImpl();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // Setter for testing / DI
    public void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
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
        String query = request.getParameter("q");
        PrintWriter out = response.getWriter();

        if (pathInfo != null && !pathInfo.equals("/") && !pathInfo.equals("/search")) {
            // Check for /api/patients/{id}
            String idStr = pathInfo.substring(1).trim();
            try {
                int patientId = Integer.parseInt(idStr);
                Patient patient = patientDAO.getPatientById(patientId);
                if (patient != null) {
                    JsonObject result = new JsonObject();
                    result.addProperty("status", "success");
                    result.add("data", gson.toJsonTree(patient));
                    out.print(gson.toJson(result));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = new JsonObject();
                    error.addProperty("status", "error");
                    error.addProperty("message", "Patient not found with ID: " + patientId);
                    out.print(gson.toJson(error));
                }
                return;
            } catch (NumberFormatException ignored) {
                // Treat as search query or general request
            }
        }

        // Search with ?q= or /search?q= or return all
        List<Patient> list;
        if (query != null && !query.trim().isEmpty()) {
            list = patientDAO.searchPatientsUniversal(query.trim());
        } else {
            list = patientDAO.getAllPatients();
        }

        JsonObject result = new JsonObject();
        result.addProperty("status", "success");
        result.addProperty("count", list.size());
        result.add("data", gson.toJsonTree(list));
        out.print(gson.toJson(result));
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
    }
}
