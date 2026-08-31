<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    Appointment appt = (Appointment) request.getAttribute("appointment");
    String searchError = (String) request.getAttribute("searchError");
    String searchedNumber = (String) request.getAttribute("searchedNumber");
    String success = request.getParameter("success");
    String notified = request.getParameter("notified");
    String patientContact = (String) request.getAttribute("patientContact");
    String smsPreview = (String) request.getAttribute("smsPreview");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Appointment Details - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Elevated UI Design System -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/theme.css">
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark navbar-clinic sticky-top">
    <div class="container-fluid px-4">
        <a class="navbar-brand d-flex align-items-center fw-bold" href="<%= request.getContextPath() %>/dashboard">
            <i class="bi bi-hospital fs-3 me-2"></i>
            <span>Sunrise Dental Clinic</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-3">
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/dashboard">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/patients">
                        <i class="bi bi-people-fill me-1"></i>Patients
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/appointments?action=new">
                        <i class="bi bi-calendar-plus me-1"></i>New Appointment
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/appointments?action=search">
                        <i class="bi bi-search me-1"></i>Search Records
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/reports">
                        <i class="bi bi-graph-up me-1"></i>Reports & Analytics
                    </a>
                </li>
                <% if (isAdmin) { %>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle text-white-50" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-gear me-1"></i>Administration
                    </a>
                    <ul class="dropdown-menu shadow-sm">
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/users"><i class="bi bi-people me-2"></i>Manage Staff Accounts</a></li>
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/dentists"><i class="bi bi-person-badge me-2"></i>Manage Dentists</a></li>
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/treatments"><i class="bi bi-clipboard2-pulse me-2"></i>Manage Treatments</a></li>
                    </ul>
                </li>
                <% } %>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/views/help.jsp">
                        <i class="bi bi-question-circle me-1"></i>Help
                    </a>
                </li>
            </ul>

            <div class="d-flex align-items-center text-white gap-3">
                <button id="theme-toggle-btn" class="theme-toggle-btn" title="Toggle Theme" aria-label="Toggle Theme">
                    <i class="bi bi-moon-stars-fill"></i>
                </button>
                <div class="text-end d-none d-md-block">
                    <div class="fw-semibold small"><%= staffName %></div>
                    <small class="badge bg-light text-primary"><%= role %></small>
                </div>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm fw-semibold">Logout</a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">

    <!-- Search Section -->
    <div class="row justify-content-center mb-4">
        <div class="col-12 col-lg-8">
            <div class="card clinic-card p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-search me-2 text-primary"></i>Find Appointment by Number</h5>
                <form action="<%= request.getContextPath() %>/appointments" method="get" class="row g-2 align-items-center">
                    <input type="hidden" name="action" value="search">
                    <div class="col-8 col-sm-9">
                        <div class="form-floating">
                            <input type="text"
                                   class="form-control"
                                   id="appointmentNumber"
                                   name="appointmentNumber"
                                   placeholder="Appointment Number"
                                   value="<%= searchedNumber != null ? searchedNumber : "" %>" required>
                            <label for="appointmentNumber">Enter Appointment Number (e.g. 1, 101)</label>
                        </div>
                    </div>
                    <div class="col-4 col-sm-3 d-grid">
                        <button type="submit" class="btn btn-primary-gradient py-3 fw-semibold shadow-sm">
                            <i class="bi bi-search me-1"></i>Search
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Success Message if redirected after creation -->
    <% if ("registered".equals(success)) { %>
        <div class="row justify-content-center mb-3">
            <div class="col-12 col-lg-8">
                <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>
                    <strong>Appointment Registered Successfully!</strong> Record is now scheduled and ready for billing.
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
        </div>
    <% } %>

    <!-- Notification Service Alert Toast / Badge (Task B) -->
    <% if ("true".equals(notified) || appt != null) { %>
        <div class="row justify-content-center mb-3">
            <div class="col-12 col-lg-8">
                <div class="card clinic-card p-3 shadow-sm border-start border-success border-4">
                    <div class="d-flex align-items-center justify-content-between mb-1">
                        <div class="d-flex align-items-center">
                            <span class="badge bg-success me-2"><i class="bi bi-send-check me-1"></i>SMS / Email Alert Dispatched</span>
                            <span class="fw-bold text-success">Automated Patient Notification Active</span>
                        </div>
                        <small class="text-muted"><i class="bi bi-telephone me-1"></i>Recipient: <%= patientContact != null ? patientContact : "Registered Mobile" %></small>
                    </div>
                    <div class="small text-secondary bg-body-secondary p-2 rounded border mt-2">
                        <i class="bi bi-chat-left-dots text-primary me-1"></i>
                        <strong>Simulated Notification Payload:</strong>
                        <%= smsPreview != null ? smsPreview : ("Dear " + (appt != null ? appt.getPatientName() : "Patient") + ", your appointment #" + (appt != null ? appt.getAppointmentNumber() : "") + " is confirmed. Sunrise Dental Clinic.") %>
                    </div>
                </div>
            </div>
        </div>
    <% } %>

    <!-- Search Error Alert -->
    <% if (searchError != null) { %>
        <div class="row justify-content-center mb-3">
            <div class="col-12 col-lg-8">
                <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
                    <i class="bi bi-exclamation-circle-fill me-2"></i>
                    <%= searchError %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
        </div>
    <% } %>

    <!-- Appointment Card View -->
    <% if (appt != null) { %>
    <div class="row justify-content-center">
        <div class="col-12 col-lg-8">
            <div class="card clinic-card p-4 p-md-5 mb-4 overflow-hidden">
                <div class="d-flex justify-content-between align-items-center pb-3 mb-4 border-bottom">
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-primary text-white fw-bold fs-6">#<%= appt.getAppointmentNumber() %></span>
                        <h4 class="mb-0 fw-bold">Appointment Record</h4>
                    </div>
                    <div>
                        <% if ("COMPLETED".equalsIgnoreCase(appt.getStatus())) { %>
                            <span class="badge bg-success fs-6"><i class="bi bi-check-circle me-1"></i>COMPLETED</span>
                        <% } else { %>
                            <span class="badge bg-warning text-dark fs-6"><i class="bi bi-clock me-1"></i><%= appt.getStatus() %></span>
                        <% } %>
                    </div>
                </div>

                <!-- Patient Info Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <h6 class="text-primary text-uppercase fw-bold border-bottom pb-2 mb-3">
                            <i class="bi bi-person-fill me-1"></i>Patient Information
                        </h6>
                    </div>
                    <div class="col-sm-6 mb-2">
                        <small class="text-muted d-block">Patient Full Name</small>
                        <div class="fs-5 fw-bold d-flex align-items-center gap-2">
                            <span><%= appt.getPatientName() != null ? appt.getPatientName() : "Patient #" + appt.getPatientId() %></span>
                            <a href="<%= request.getContextPath() %>/patients?action=view&id=<%= appt.getPatientId() %>" class="btn btn-outline-primary btn-sm" title="View Patient Profile & Clinical Timeline">
                                <i class="bi bi-clock-history me-1"></i>History
                            </a>
                        </div>
                    </div>
                    <div class="col-sm-6 mb-2">
                        <small class="text-muted d-block">System Patient ID</small>
                        <div class="fs-6 fw-semibold text-secondary">PID-<%= appt.getPatientId() %></div>
                    </div>
                </div>

                <!-- Medical & Treatment Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <h6 class="text-primary text-uppercase fw-bold border-bottom pb-2 mb-3">
                            <i class="bi bi-heart-pulse-fill me-1"></i>Clinical Details
                        </h6>
                    </div>
                    <div class="col-sm-6 mb-3">
                        <small class="text-muted d-block">Attending Dentist</small>
                        <div class="fs-6 fw-bold"><i class="bi bi-person-badge text-primary me-1"></i><%= appt.getDentistName() != null ? appt.getDentistName() : "Dentist #" + appt.getDentistId() %></div>
                    </div>
                    <div class="col-sm-6 mb-3">
                        <small class="text-muted d-block">Treatment Procedure</small>
                        <div class="fs-6 fw-bold"><i class="bi bi-bandaid text-info me-1"></i><%= appt.getTreatmentName() != null ? appt.getTreatmentName() : "Treatment #" + appt.getTreatmentId() %></div>
                    </div>
                    <div class="col-sm-6 mb-3">
                        <small class="text-muted d-block">Scheduled Date</small>
                        <div class="fs-6 fw-semibold"><i class="bi bi-calendar-event text-secondary me-1"></i><%= appt.getAppointmentDate() %></div>
                    </div>
                    <div class="col-sm-6 mb-3">
                        <small class="text-muted d-block">Scheduled Time</small>
                        <div class="fs-6 fw-semibold"><i class="bi bi-clock text-secondary me-1"></i><%= appt.getAppointmentTime() %></div>
                    </div>
                    <div class="col-sm-6 mb-2">
                        <small class="text-muted d-block">Standard Procedure Fee</small>
                        <div class="fs-5 fw-bold text-success">$<%= String.format("%.2f", appt.getCost()) %></div>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                    <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
                    </a>
                    <div class="d-flex gap-2">
                        <a href="<%= request.getContextPath() %>/billing?appointmentNumber=<%= appt.getAppointmentNumber() %>"
                           class="btn btn-success-gradient px-4 fw-semibold shadow-sm">
                            <i class="bi bi-receipt me-1"></i>View / Generate Invoice
                        </a>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <% } %>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
