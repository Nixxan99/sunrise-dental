package com.sunrisedental.controller;

import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.dao.impl.UserDAOImpl;
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
 * Administrative Controller for User Account Management (Role-based security ethics).
 * Restricted strictly to users with role 'ADMIN'.
 */
@WebServlet(name = "UserManagementServlet", urlPatterns = {"/admin/users"})
public class UserManagementServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        this.userDAO = new UserDAOImpl();
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request, response)) {
            return;
        }

        List<User> users = userDAO.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/views/user-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "create";
        }

        switch (action) {
            case "create":
                handleCreateUser(request, response);
                break;
            case "reset":
                handleResetPassword(request, response);
                break;
            case "delete":
                handleDeleteUser(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/users");
                break;
        }
    }

    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String role = request.getParameter("role");
        String tempPassword = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                tempPassword == null || tempPassword.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required to register a staff account.");
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/views/user-management.jsp").forward(request, response);
            return;
        }

        User existing = userDAO.getUserByUsername(username.trim());
        if (existing != null) {
            request.setAttribute("errorMessage", "Username '" + username.trim() + "' is already taken.");
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/views/user-management.jsp").forward(request, response);
            return;
        }

        User newUser = new User(
                username.trim(),
                tempPassword.trim(),
                fullName.trim(),
                role != null ? role.trim().toUpperCase() : "STAFF",
                true // Must change password on first login
        );

        boolean success = userDAO.createUser(newUser);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=created");
        } else {
            request.setAttribute("errorMessage", "Database error creating user account. Please try again.");
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/views/user-management.jsp").forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userIdStr = request.getParameter("userId");
        String tempPassword = request.getParameter("tempPassword");

        if (tempPassword == null || tempPassword.trim().isEmpty()) {
            tempPassword = "DentalStaff123!";
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            userDAO.resetPassword(userId, tempPassword.trim());
            response.sendRedirect(request.getContextPath() + "/admin/users?success=reset&temp=" + tempPassword.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_user");
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userIdStr = request.getParameter("userId");
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        try {
            int userId = Integer.parseInt(userIdStr);
            if (currentUser != null && currentUser.getUserId() == userId) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=self_delete");
                return;
            }

            userDAO.deleteUser(userId);
            response.sendRedirect(request.getContextPath() + "/admin/users?success=deleted");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_user");
        }
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
                    "Access Denied: Administrator role required to access user management.");
            return false;
        }
        return true;
    }
}
