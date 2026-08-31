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
    <style>
        body {
            background: linear-gradient(135deg, #0d6efd 0%, #004085 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .reset-card {
            border: none;
            border-radius: 1rem;
            box-shadow: 0 15px 35px rgba(0,0,0,0.25);
            width: 100%;
            max-width: 480px;
        }
        .strength-bar {
            height: 6px;
            border-radius: 3px;
            transition: width 0.3s ease;
        }
    </style>
</head>
<body>

<div class="container p-3">
    <div class="row justify-content-center">
        <div class="col-12 col-sm-10 col-md-8 col-lg-6">

            <div class="card reset-card bg-white p-4 p-md-5">

                <div class="text-center mb-4">
                    <div class="bg-warning bg-opacity-10 text-warning d-inline-flex p-3 rounded-circle mb-3">
                        <i class="bi bi-shield-lock-fill fs-1"></i>
                    </div>
                    <h3 class="fw-bold text-dark mb-1">Set New Password</h3>
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

                    <div class="mb-3">
                        <label for="newPassword" class="form-label fw-semibold">New Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-key"></i></span>
                            <input type="password" class="form-control" id="newPassword" name="newPassword"
                                   placeholder="Minimum 6 characters" minlength="6" required autofocus>
                        </div>
                        <div class="progress mt-2" style="height: 6px;">
                            <div id="strengthBar" class="progress-bar bg-danger" role="progressbar" style="width: 0%;"></div>
                        </div>
                        <small id="strengthText" class="text-muted d-block mt-1">Password strength: Weak</small>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label fw-semibold">Confirm New Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-check2-circle"></i></span>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                   placeholder="Re-type your password" minlength="6" required>
                        </div>
                        <small id="matchFeedback" class="text-danger d-none mt-1">Passwords do not match.</small>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary btn-lg fw-semibold shadow-sm">
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
</body>
</html>
