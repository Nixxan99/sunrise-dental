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

/**
 * Controller handling user authentication, login lifecycle, and password change interception.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        this.userDAO = new UserDAOImpl();
    }

    // Setter for unit testing and dependency injection
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser.isMustChangePassword()) {
                response.sendRedirect(request.getContextPath() + "/reset-password");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // Forward to login page view
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please provide both username and password.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        User authenticatedUser = userDAO.authenticate(username.trim(), password);

        if (authenticatedUser != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", authenticatedUser);
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("fullName", authenticatedUser.getFullName());
            session.setAttribute("role", authenticatedUser.getRole());

            // Set session timeout (30 minutes)
            session.setMaxInactiveInterval(30 * 60);

            // Password Security Interception: First login password change
            if (authenticatedUser.isMustChangePassword()) {
                response.sendRedirect(request.getContextPath() + "/reset-password");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } else {
            request.setAttribute("errorMessage", "Invalid credentials. Please check your username and password.");
            request.setAttribute("enteredUsername", username.trim());
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
