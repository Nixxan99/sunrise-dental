package com.sunrisedental.controller;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.impl.PatientDAOImpl;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.PatientAppointmentHistoryItem;
import com.sunrisedental.model.User;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet for persistent patient management, full CRUD operations, patient lookup, and appointment history tracking.
 * Handles /patients endpoint.
 */
@WebServlet(name = "PatientServlet", urlPatterns = {"/patients"})
public class PatientServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.patientDAO = new PatientDAOImpl();
    }

    /**
     * Package-private setter for unit testing dependency injection.
     */
    void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action.toLowerCase()) {
            case "view":
                handleViewHistory(request, response);
                break;
            case "edit":
                handleEditPatient(request, response);
                break;
            case "delete":
                handleDeletePatient(request, response);
                break;
            case "search":
            case "list":
            default:
                handleListPatients(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String patientIdStr = request.getParameter("patientId");
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String contactNumber = request.getParameter("contactNumber");
        String email = request.getParameter("email");
        String nic = request.getParameter("nic");

        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Patient full name is required.");
            handleListPatients(request, response);
            return;
        }

        if (contactNumber == null || !ValidationUtil.isValidPhoneNumber(contactNumber)) {
            request.setAttribute("errorMessage", "Please provide a valid contact number (e.g. 0771234567).");
            handleListPatients(request, response);
            return;
        }

        if (nic != null && !nic.trim().isEmpty() && !ValidationUtil.isValidNic(nic.trim())) {
            request.setAttribute("errorMessage", "Invalid NIC format. Please enter a valid Sri Lankan NIC (e.g. 199012345678 or 851234567V).");
            handleListPatients(request, response);
            return;
        }

        Patient patient = new Patient(
                fullName.trim(),
                address != null ? address.trim() : "",
                contactNumber.trim(),
                email != null ? email.trim() : "",
                nic != null ? nic.trim().toUpperCase() : ""
        );

        if (patientIdStr != null && !patientIdStr.trim().isEmpty()) {
            try {
                int patientId = Integer.parseInt(patientIdStr.trim());
                patient.setPatientId(patientId);
                boolean updated = patientDAO.updatePatient(patient);
                if (updated) {
                    response.sendRedirect(request.getContextPath() + "/patients?action=list&success=updated");
                } else {
                    request.setAttribute("errorMessage", "Failed to update patient profile.");
                    handleListPatients(request, response);
                }
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid patient identifier.");
                handleListPatients(request, response);
                return;
            }
        }

        // Register new patient
        int genId = patientDAO.registerPatient(patient);
        if (genId > 0) {
            response.sendRedirect(request.getContextPath() + "/patients?action=list&success=created");
        } else {
            request.setAttribute("errorMessage", "Failed to register patient profile.");
            handleListPatients(request, response);
        }
    }

    private void handleDeletePatient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int patientId = Integer.parseInt(idStr.trim());
                boolean deleted = patientDAO.deletePatient(patientId);
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/patients?action=list&success=deleted");
                    return;
                } else {
                    request.setAttribute("errorMessage", "Unable to delete patient profile (ID #" + patientId + ").");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid patient ID format.");
            }
        }
        handleListPatients(request, response);
    }

    private void handleListPatients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("q");
        List<Patient> patients;
        if (query != null && !query.trim().isEmpty()) {
            patients = patientDAO.searchPatientsUniversal(query.trim());
            request.setAttribute("searchQuery", query.trim());
        } else {
            patients = patientDAO.getAllPatients();
        }
        request.setAttribute("patients", patients);
        request.getRequestDispatcher("/views/patient-list.jsp").forward(request, response);
    }

    private void handleViewHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/patients?action=list");
            return;
        }

        try {
            int patientId = Integer.parseInt(idStr.trim());
            Patient patient = patientDAO.getPatientById(patientId);
            if (patient == null) {
                request.setAttribute("errorMessage", "Patient with ID #" + patientId + " not found.");
                handleListPatients(request, response);
                return;
            }

            List<PatientAppointmentHistoryItem> history = patientDAO.getPatientAppointmentHistory(patientId);
            request.setAttribute("patient", patient);
            request.setAttribute("history", history);
            request.getRequestDispatcher("/views/patient-history.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/patients?action=list");
        }
    }

    private void handleEditPatient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int patientId = Integer.parseInt(idStr.trim());
                Patient patient = patientDAO.getPatientById(patientId);
                request.setAttribute("editPatient", patient);
            } catch (NumberFormatException ignored) {
            }
        }
        handleListPatients(request, response);
    }
}
