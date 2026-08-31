package com.sunrisedental.controller;

import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.dao.impl.TreatmentDAOImpl;
import com.sunrisedental.model.Treatment;
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
 * Controller for Treatment catalog CRUD operations (Task B Administration).
 * Restricted to users with role 'ADMIN'.
 */
@WebServlet(name = "TreatmentServlet", urlPatterns = {"/admin/treatments"})
public class TreatmentServlet extends HttpServlet {

    private TreatmentDAO treatmentDAO;

    @Override
    public void init() {
        this.treatmentDAO = new TreatmentDAOImpl();
    }

    public void setTreatmentDAO(TreatmentDAO treatmentDAO) {
        this.treatmentDAO = treatmentDAO;
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

        String treatmentIdStr = request.getParameter("treatmentId");
        String treatmentName = request.getParameter("treatmentName");
        String feeStr = request.getParameter("standardFee");

        if (treatmentName == null || treatmentName.trim().isEmpty() ||
                feeStr == null || feeStr.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Treatment name and standard fee are required.");
            handleList(request, response);
            return;
        }

        double standardFee;
        try {
            standardFee = Double.parseDouble(feeStr.trim());
            if (standardFee < 0) {
                request.setAttribute("errorMessage", "Treatment fee cannot be negative.");
                handleList(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid format for standard fee. Please enter a valid decimal number.");
            handleList(request, response);
            return;
        }

        Treatment treatment = new Treatment();
        treatment.setTreatmentName(treatmentName.trim());
        treatment.setStandardFee(standardFee);

        if (treatmentIdStr != null && !treatmentIdStr.trim().isEmpty()) {
            try {
                int treatmentId = Integer.parseInt(treatmentIdStr.trim());
                treatment.setTreatmentId(treatmentId);
                boolean success = treatmentDAO.updateTreatment(treatment);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/treatments?success=updated");
                } else {
                    request.setAttribute("errorMessage", "Failed to update treatment details.");
                    handleList(request, response);
                }
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid treatment ID format.");
                handleList(request, response);
                return;
            }
        }

        boolean success = treatmentDAO.addTreatment(treatment);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/treatments?success=created");
        } else {
            request.setAttribute("errorMessage", "Failed to add new treatment.");
            handleList(request, response);
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Treatment> treatments = treatmentDAO.getAllTreatments();
        request.setAttribute("treatments", treatments);
        request.getRequestDispatcher("/views/treatment-management.jsp").forward(request, response);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            idStr = request.getParameter("treatmentId");
        }

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                Treatment treatment = treatmentDAO.getTreatmentById(id);
                if (treatment != null) {
                    request.setAttribute("editTreatment", treatment);
                } else {
                    request.setAttribute("errorMessage", "Treatment not found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid treatment ID.");
            }
        }
        handleList(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            idStr = request.getParameter("treatmentId");
        }

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean deleted = treatmentDAO.deleteTreatment(id);
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/admin/treatments?success=deleted");
                    return;
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/treatments?error=cannot_delete");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/treatments?error=invalid_id");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/treatments");
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
                    "Access Denied: Administrator role required to manage treatments.");
            return false;
        }
        return true;
    }
}
