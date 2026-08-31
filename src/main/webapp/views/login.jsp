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
    <style>
        body {
            background: linear-gradient(135deg, #0d6efd 0%, #0dcaf0 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .login-card {
            border-radius: 1rem;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            border: none;
            overflow: hidden;
        }
        .clinic-brand-header {
            background-color: #f8f9fa;
            border-bottom: 2px solid #e9ecef;
        }
        .btn-primary-gradient {
            background: linear-gradient(90deg, #0d6efd 0%, #0b5ed7 100%);
            border: none;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-12 col-md-8 col-lg-5">
            <div class="card login-card bg-white">
                <div class="clinic-brand-header p-4 text-center">
                    <div class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle mb-3" style="width: 60px; height: 60px;">
                        <i class="bi bi-hospital fs-2"></i>
                    </div>
                    <h3 class="fw-bold text-dark mb-1">Sunrise Dental Clinic</h3>
                    <p class="text-muted small mb-0">Clinic Management & Billing System (CIS6003)</p>
                </div>

                <div class="card-body p-4 p-md-5">
                    <h5 class="card-title text-center mb-4 fw-semibold text-secondary">
                        <i class="bi bi-shield-lock me-2"></i>Staff Portal Login
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
                        <div class="mb-3">
                            <label for="username" class="form-label fw-medium text-dark">
                                <i class="bi bi-person-fill me-1 text-primary"></i>Username
                            </label>
                            <input type="text"
                                   class="form-control form-control-lg fs-6"
                                   id="username"
                                   name="username"
                                   placeholder="Enter staff username"
                                   value="<%= request.getAttribute("enteredUsername") != null ? request.getAttribute("enteredUsername") : "" %>"
                                   required autofocus>
                        </div>

                        <div class="mb-4">
                            <label for="password" class="form-label fw-medium text-dark">
                                <i class="bi bi-key-fill me-1 text-primary"></i>Password
                            </label>
                            <input type="password"
                                   class="form-control form-control-lg fs-6"
                                   id="password"
                                   name="password"
                                   placeholder="Enter password"
                                   required>
                        </div>

                        <div class="d-grid gap-2 mb-3">
                            <button type="submit" class="btn btn-primary btn-primary-gradient btn-lg fs-6 fw-semibold shadow-sm">
                                <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
                            </button>
                        </div>
                    </form>
                </div>

                <div class="card-footer bg-light text-center py-3">
                    <small class="text-muted">
                        <i class="bi bi-info-circle me-1"></i>Default accounts: <strong>admin / admin123</strong> or <strong>staff / staff123</strong>
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
