package com.sunrisedental.controller;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.impl.DentistDAOImpl;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Dentist CRUD operations (Task B Administration).
 * Restricted to users with role 'ADMIN'.
 */
@WebServlet(name = "DentistServlet", urlPatterns = {"/admin/dentists"})
public class DentistServlet extends HttpServlet {

    private DentistDAO dentistDAO;

    @Override
    public void init() {
        this.dentistDAO = new DentistDAOImpl();
    }

    public void setDentistDAO(DentistDAO dentistDAO) {
        this.dentistDAO = dentistDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "edit":
                handleEdit(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            case "list":
            default:
                handleList(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request, response)) {
            return;
        }

        String dentistIdStr = request.getParameter("dentistId");
        String name = request.getParameter("name");
        String specialization = request.getParameter("specialization");
        String contactNumber = request.getParameter("contactNumber");

        if (name == null || name.trim().isEmpty() ||
                specialization == null || specialization.trim().isEmpty() ||
                contactNumber == null || contactNumber.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All dentist fields (Name, Specialization, Contact Number) are required.");
            handleList(request, response);
            return;
        }

        Dentist dentist = new Dentist();
        dentist.setName(name.trim());
        dentist.setSpecialization(specialization.trim());
        dentist.setContactNumber(contactNumber.trim());

        if (dentistIdStr != null && !dentistIdStr.trim().isEmpty()) {
            try {
                int dentistId = Integer.parseInt(dentistIdStr.trim());
                dentist.setDentistId(dentistId);
                boolean success = dentistDAO.updateDentist(dentist);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/dentists?success=updated");
                } else {
                    request.setAttribute("errorMessage", "Failed to update dentist details.");
                    handleList(request, response);
                }
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid dentist ID format.");
                handleList(request, response);
                return;
            }
        }

        boolean success = dentistDAO.addDentist(dentist);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/dentists?success=created");
        } else {
            request.setAttribute("errorMessage", "Failed to add new dentist.");
            handleList(request, response);
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Dentist> dentists = dentistDAO.getAllDentists();
        request.setAttribute("dentists", dentists);
        request.getRequestDispatcher("/views/dentist-management.jsp").forward(request, response);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            idStr = request.getParameter("dentistId");
        }

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                Dentist dentist = dentistDAO.getDentistById(id);
                if (dentist != null) {
                    request.setAttribute("editDentist", dentist);
                } else {
                    request.setAttribute("errorMessage", "Dentist not found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid dentist ID.");
            }
        }
        handleList(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            idStr = request.getParameter("dentistId");
        }

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean deleted = dentistDAO.deleteDentist(id);
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/admin/dentists?success=deleted");
                    return;
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/dentists?error=cannot_delete");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/dentists?error=invalid_id");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/dentists");
    }

    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return false;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access Denied: Administrator role required to manage dentists.");
            return false;
        }
        return true;
    }
}
