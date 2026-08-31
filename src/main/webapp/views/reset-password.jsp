<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String fullName = currentUser != null ? currentUser.getFullName() : "Staff Member";
    String username = currentUser != null ? currentUser.getUsername() : "";
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Set New Password - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Elevated UI Design System -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/theme.css">
    <style>
        body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }
        .theme-toggle-floating {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1050;
        }
    </style>
</head>
<body class="login-wrapper">

<!-- Fixed Floating Theme Toggle Button -->
<div class="theme-toggle-floating">
    <button id="theme-toggle-btn" class="theme-toggle-btn shadow" title="Toggle Theme" aria-label="Toggle Theme">
        <i class="bi bi-moon-stars-fill"></i>
    </button>
</div>

<div class="container p-3">
    <div class="row justify-content-center">
        <div class="col-12 col-sm-10 col-md-8 col-lg-5">

            <div class="card clinic-card p-4 p-md-5">

                <div class="text-center mb-4">
                    <div class="badge-subtle-warning d-inline-flex p-3 rounded-circle mb-3">
                        <i class="bi bi-shield-lock-fill fs-1 text-warning"></i>
                    </div>
                    <h3 class="fw-bold mb-1">Set New Password</h3>
                    <p class="text-muted small mb-0">
                        Hello, <strong><%= fullName %></strong> (<%= username %>). For security ethics and policy compliance, please create a new personal password before accessing the system.
                    </p>
                </div>

                <% if (errorMessage != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <%= errorMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <form action="<%= request.getContextPath() %>/reset-password" method="post" id="resetForm">

                    <div class="form-floating mb-3">
                        <input type="password" class="form-control" id="newPassword" name="newPassword"
                               placeholder="Minimum 6 characters" minlength="6" required autofocus>
                        <label for="newPassword">New Password <span class="text-danger">*</span></label>
                        <div class="progress mt-2" style="height: 6px;">
                            <div id="strengthBar" class="progress-bar bg-danger" role="progressbar" style="width: 0%;"></div>
                        </div>
                        <small id="strengthText" class="text-muted d-block mt-1">Password strength: Weak</small>
                    </div>

                    <div class="form-floating mb-4">
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               placeholder="Re-type your password" minlength="6" required>
                        <label for="confirmPassword">Confirm New Password <span class="text-danger">*</span></label>
                        <small id="matchFeedback" class="text-danger d-none mt-1">Passwords do not match.</small>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary-gradient py-3 fw-semibold shadow-sm">
                            <i class="bi bi-shield-check me-1"></i>Save Password & Proceed
                        </button>
                        <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-box-arrow-right me-1"></i>Cancel and Logout
                        </a>
                    </div>

                </form>

            </div>

        </div>
    </div>
</div>

<script>
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const strengthBar = document.getElementById('strengthBar');
    const strengthText = document.getElementById('strengthText');
    const matchFeedback = document.getElementById('matchFeedback');

    newPassword.addEventListener('input', function () {
        const val = newPassword.value;
        let score = 0;
        if (val.length >= 6) score += 25;
        if (val.length >= 10) score += 25;
        if (/[A-Z]/.test(val) && /[a-z]/.test(val)) score += 25;
        if (/[0-9]/.test(val) || /[^A-Za-z0-9]/.test(val)) score += 25;

        strengthBar.style.width = score + '%';
        if (score <= 25) {
            strengthBar.className = 'progress-bar bg-danger';
            strengthText.textContent = 'Password strength: Weak (min 6 characters)';
        } else if (score <= 50) {
            strengthBar.className = 'progress-bar bg-warning';
            strengthText.textContent = 'Password strength: Fair';
        } else if (score <= 75) {
            strengthBar.className = 'progress-bar bg-info';
            strengthText.textContent = 'Password strength: Good';
        } else {
            strengthBar.className = 'progress-bar bg-success';
            strengthText.textContent = 'Password strength: Strong';
        }
    });

    confirmPassword.addEventListener('input', function () {
        if (confirmPassword.value !== newPassword.value) {
            matchFeedback.classList.remove('d-none');
        } else {
            matchFeedback.classList.add('d-none');
        }
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
