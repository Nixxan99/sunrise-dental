<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Login - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Elevated UI Design System -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/theme.css">
    <style>
        .login-bg {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .theme-toggle-fixed {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1050;
        }
    </style>
</head>
<body class="login-bg">

<!-- Persistent Dark Mode Switcher -->
<div class="theme-toggle-fixed">
    <button id="theme-toggle-btn" class="theme-toggle-btn shadow" title="Toggle Theme" aria-label="Toggle Theme">
        <i class="bi bi-moon-stars-fill"></i>
    </button>
</div>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-12 col-md-8 col-lg-5">
            <div class="card clinic-card shadow-lg overflow-hidden border-0">
                <div class="p-4 p-md-5 text-center bg-primary bg-opacity-10 border-bottom">
                    <div class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle mb-3 shadow-sm" style="width: 68px; height: 68px;">
                        <i class="bi bi-hospital fs-2"></i>
                    </div>
                    <h3 class="fw-bold text-primary mb-1">Sunrise Dental Clinic</h3>
                    <p class="text-muted small mb-0">Clinic Management & Billing System (CIS6003)</p>
                </div>

                <div class="card-body p-4 p-md-5">
                    <h5 class="card-title text-center mb-4 fw-bold">
                        <i class="bi bi-shield-lock text-primary me-2"></i>Staff Portal Login
                    </h5>

                    <!-- Error Alert -->
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <%= request.getAttribute("errorMessage") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <!-- Unauthorized Alert -->
                    <% if ("unauthorized".equals(request.getParameter("error"))) { %>
                        <div class="alert alert-warning alert-dismissible fade show" role="alert">
                            <i class="bi bi-lock-fill me-2"></i>
                            Please log in with your staff account to access this page.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <!-- Logout Success Alert -->
                    <% if ("logged_out".equals(request.getParameter("message")) || "true".equals(request.getParameter("logout"))) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            You have been logged out successfully.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/login" method="post">
                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   id="username"
                                   name="username"
                                   placeholder="Username"
                                   value="<%= request.getAttribute("enteredUsername") != null ? request.getAttribute("enteredUsername") : "" %>"
                                   required autofocus>
                            <label for="username">
                                <i class="bi bi-person me-1"></i>Username
                            </label>
                        </div>

                        <div class="form-floating mb-4">
                            <input type="password"
                                   class="form-control"
                                   id="password"
                                   name="password"
                                   placeholder="Password"
                                   required>
                            <label for="password">
                                <i class="bi bi-key me-1"></i>Password
                            </label>
                        </div>

                        <div class="d-grid gap-2 mb-3">
                            <button type="submit" class="btn btn-primary-gradient btn-lg fs-6 fw-bold shadow-sm py-3">
                                <i class="bi bi-box-arrow-in-right me-2"></i>Sign In to Portal
                            </button>
                        </div>
                    </form>
                </div>

                <div class="card-footer bg-body-tertiary text-center py-3 border-top">
                    <small class="text-muted">
                        <i class="bi bi-info-circle me-1"></i>Default credentials: <strong>admin / admin123</strong> or <strong>staff / staff123</strong>
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
