package com.sunrisedental.controller;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.dao.impl.AppointmentDAOImpl;
import com.sunrisedental.dao.impl.DentistDAOImpl;
import com.sunrisedental.dao.impl.PatientDAOImpl;
import com.sunrisedental.dao.impl.TreatmentDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller handling appointment registration, search, and dashboard aggregation.
 */
@WebServlet(name = "AppointmentServlet", urlPatterns = {"/appointments"})
public class AppointmentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AppointmentServlet.class.getName());

    private AppointmentDAO appointmentDAO;
    private DentistDAO dentistDAO;
    private TreatmentDAO treatmentDAO;
    private PatientDAO patientDAO;

    @Override
    public void init() {
        this.appointmentDAO = new AppointmentDAOImpl();
        this.dentistDAO = new DentistDAOImpl();
        this.treatmentDAO = new TreatmentDAOImpl();
        this.patientDAO = new PatientDAOImpl();
    }

    // Setters for unit testing and DI
    public void setAppointmentDAO(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public void setDentistDAO(DentistDAO dentistDAO) {
        this.dentistDAO = dentistDAO;
    }

    public void setTreatmentDAO(TreatmentDAO treatmentDAO) {
        this.treatmentDAO = treatmentDAO;
    }

    public void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                showNewAppointmentForm(request, response);
                break;
            case "search":
            case "view":
                searchAppointment(request, response);
                break;
            case "list":
            default:
                listAppointments(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String patientName = request.getParameter("patientName");
        String address = request.getParameter("address");
        String contactNumber = request.getParameter("contactNumber");
        String dentistIdStr = request.getParameter("dentistId");
        String treatmentIdStr = request.getParameter("treatmentId");
        String appointmentDateStr = request.getParameter("appointmentDate");
        String appointmentTimeStr = request.getParameter("appointmentTime");

        // Validate basic text fields
        if (!ValidationUtil.isNotEmpty(patientName)) {
            forwardWithValidationError(request, response, "Patient full name is required.");
            return;
        }

        if (!ValidationUtil.isValidPhoneNumber(contactNumber)) {
            forwardWithValidationError(request, response,
                    "Invalid phone number format. Please provide a valid Sri Lankan number (e.g., 0712345678, +94771234567).");
            return;
        }

        if (!ValidationUtil.isValidAppointmentDate(appointmentDateStr)) {
            forwardWithValidationError(request, response,
                    "Invalid appointment date. The appointment date cannot be in the past.");
            return;
        }

        int dentistId;
        int treatmentId;
        try {
            dentistId = Integer.parseInt(dentistIdStr);
            treatmentId = Integer.parseInt(treatmentIdStr);
        } catch (NumberFormatException e) {
            forwardWithValidationError(request, response, "Please select a valid Dentist and Treatment.");
            return;
        }

        LocalDate appointmentDate;
        LocalTime appointmentTime;
        try {
            appointmentDate = LocalDate.parse(appointmentDateStr.trim());
            // Handle HH:mm or HH:mm:ss format
            String timeClean = appointmentTimeStr.trim();
            if (timeClean.length() == 5) {
                timeClean = timeClean + ":00";
            }
            appointmentTime = LocalTime.parse(timeClean);
        } catch (DateTimeParseException e) {
            forwardWithValidationError(request, response, "Invalid date or time format provided.");
            return;
        }

        try {
            // Find or create patient
            Patient patient = patientDAO.getPatientByContactNumber(contactNumber.trim());
            if (patient == null) {
                patient = new Patient();
                patient.setFullName(patientName.trim());
                patient.setAddress(address != null ? address.trim() : "");
                patient.setContactNumber(contactNumber.trim());
                patientDAO.createPatient(patient);
            }

            // Construct and persist appointment
            Appointment appt = new Appointment();
            appt.setPatientId(patient.getPatientId());
            appt.setDentistId(dentistId);
            appt.setTreatmentId(treatmentId);
            appt.setAppointmentDate(appointmentDate);
            appt.setAppointmentTime(appointmentTime);
            appt.setStatus("SCHEDULED");

            boolean success = appointmentDAO.registerAppointment(appt);
            if (success) {
                response.sendRedirect(request.getContextPath()
                        + "/appointments?action=view&appointmentNumber=" + appt.getAppointmentNumber()
                        + "&success=registered");
            } else {
                forwardWithValidationError(request, response, "Database error: Could not register appointment. Please try again.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during appointment registration", e);
            forwardWithValidationError(request, response, "An unexpected system error occurred: " + e.getMessage());
        }
    }

    private void showNewAppointmentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Dentist> dentists = dentistDAO.getAllDentists();
        List<Treatment> treatments = treatmentDAO.getAllTreatments();

        request.setAttribute("dentists", dentists);
        request.setAttribute("treatments", treatments);
        request.getRequestDispatcher("/views/appointment-form.jsp").forward(request, response);
    }

    private void searchAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String apptNumberStr = request.getParameter("appointmentNumber");
        if (apptNumberStr != null && !apptNumberStr.trim().isEmpty()) {
            Appointment appt = appointmentDAO.getAppointmentByNumber(apptNumberStr.trim());
            if (appt != null) {
                request.setAttribute("appointment", appt);
            } else {
                request.setAttribute("searchError", "No appointment found with number: " + apptNumberStr.trim());
            }
            request.setAttribute("searchedNumber", apptNumberStr.trim());
        }

        request.getRequestDispatcher("/views/appointment-view.jsp").forward(request, response);
    }

    private void listAppointments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }

    private void forwardWithValidationError(HttpServletRequest request, HttpServletResponse response, String errorMsg)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMsg);
        request.setAttribute("enteredPatientName", request.getParameter("patientName"));
        request.setAttribute("enteredAddress", request.getParameter("address"));
        request.setAttribute("enteredContactNumber", request.getParameter("contactNumber"));
        request.setAttribute("enteredDentistId", request.getParameter("dentistId"));
        request.setAttribute("enteredTreatmentId", request.getParameter("treatmentId"));
        request.setAttribute("enteredDate", request.getParameter("appointmentDate"));
        request.setAttribute("enteredTime", request.getParameter("appointmentTime"));

        List<Dentist> dentists = dentistDAO.getAllDentists();
        List<Treatment> treatments = treatmentDAO.getAllTreatments();
        request.setAttribute("dentists", dentists);
        request.setAttribute("treatments", treatments);

        request.getRequestDispatcher("/views/appointment-form.jsp").forward(request, response);
    }
}
