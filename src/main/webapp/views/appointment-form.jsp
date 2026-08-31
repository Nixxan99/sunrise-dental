<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.time.LocalDate" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");

    String patientName = (String) request.getAttribute("enteredPatientName");
    String address = (String) request.getAttribute("enteredAddress");
    String contactNumber = (String) request.getAttribute("enteredContactNumber");
    String patientEmail = (String) request.getAttribute("enteredPatientEmail");
    String enteredDentistId = (String) request.getAttribute("enteredDentistId");
    String enteredTreatmentId = (String) request.getAttribute("enteredTreatmentId");
    String enteredDate = (String) request.getAttribute("enteredDate");
    String enteredTime = (String) request.getAttribute("enteredTime");

    String todayStr = LocalDate.now().toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Appointment - Sunrise Dental Clinic</title>
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
                    <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/appointments?action=new">
                        <i class="bi bi-calendar-plus me-1"></i>New Appointment
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/appointments?action=search">
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
                <span class="small text-white-50"><i class="bi bi-person-circle me-1"></i><%= staffName %></span>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm fw-semibold">Logout</a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-9">

            <div class="d-flex align-items-center justify-content-between mb-4">
                <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
                </a>
                <span class="text-muted small"><i class="bi bi-info-circle me-1"></i>Fields marked with <span class="text-danger">*</span> are mandatory</span>
            </div>

            <div class="card clinic-card p-4 p-md-5 mb-4">
                <div class="d-flex align-items-center gap-3 pb-3 mb-4 border-bottom">
                    <div class="badge-subtle-primary p-3 rounded-circle">
                        <i class="bi bi-calendar2-plus fs-3"></i>
                    </div>
                    <div>
                        <h3 class="fw-bold mb-0">Book Patient Appointment</h3>
                        <p class="text-muted small mb-0">Register appointment scheduling, patient demographics, and assigned clinical practitioner.</p>
                    </div>
                </div>

                <!-- Error Alert -->
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <%= request.getAttribute("errorMessage") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <form action="<%= request.getContextPath() %>/appointments" method="post" id="appointmentForm">

                    <!-- Section: Patient Demographics -->
                    <h6 class="text-primary text-uppercase fw-bold mb-3">
                        <i class="bi bi-person-badge me-1"></i>Patient Details
                    </h6>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="patientName" name="patientName"
                                       placeholder="Patient Full Name"
                                       value="<%= patientName != null ? patientName : "" %>" required>
                                <label for="patientName">Patient Full Name <span class="text-danger">*</span></label>
                            </div>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="contactNumber" name="contactNumber"
                                       placeholder="Contact Number"
                                       value="<%= contactNumber != null ? contactNumber : "" %>" required>
                                <label for="contactNumber">Contact Number <span class="text-danger">*</span></label>
                            </div>
                            <small class="text-muted ms-1">Format: 07XXXXXXXX or +947XXXXXXXX</small>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="email" class="form-control" id="patientEmail" name="patientEmail"
                                       placeholder="Patient Email"
                                       value="<%= patientEmail != null ? patientEmail : "" %>">
                                <label for="patientEmail">Patient Email (Optional for Gmail SMTP Alert)</label>
                            </div>
                            <small class="text-muted ms-1">Instant confirmation sent via Gmail SMTP</small>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="address" name="address"
                                       placeholder="Residential Address"
                                       value="<%= address != null ? address : "" %>">
                                <label for="address">Residential Address</label>
                            </div>
                        </div>
                    </div>

                    <!-- Section: Clinical Consultation Details -->
                    <h6 class="text-primary text-uppercase fw-bold mb-3">
                        <i class="bi bi-heart-pulse me-1"></i>Consultation & Procedure
                    </h6>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <select class="form-select" id="dentistId" name="dentistId" required>
                                    <option value="">-- Choose Assigned Doctor --</option>
                                    <% if (dentists != null) {
                                        for (Dentist d : dentists) {
                                            boolean selected = enteredDentistId != null && enteredDentistId.equals(String.valueOf(d.getDentistId()));
                                    %>
                                        <option value="<%= d.getDentistId() %>" <%= selected ? "selected" : "" %>>
                                            <%= d.getName() %> (<%= d.getSpecialization() %>)
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                                <label for="dentistId">Attending Dentist <span class="text-danger">*</span></label>
                            </div>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <select class="form-select" id="treatmentId" name="treatmentId" required>
                                    <option value="">-- Choose Dental Treatment --</option>
                                    <% if (treatments != null) {
                                        for (Treatment t : treatments) {
                                            boolean selected = enteredTreatmentId != null && enteredTreatmentId.equals(String.valueOf(t.getTreatmentId()));
                                    %>
                                        <option value="<%= t.getTreatmentId() %>" data-fee="<%= t.getStandardFee() %>" <%= selected ? "selected" : "" %>>
                                            <%= t.getTreatmentName() %> - Standard Fee: $<%= String.format("%.2f", t.getStandardFee()) %>
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                                <label for="treatmentId">Dental Treatment Procedure <span class="text-danger">*</span></label>
                            </div>
                        </div>
                    </div>

                    <!-- Section: Schedule Details -->
                    <h6 class="text-primary text-uppercase fw-bold mb-3">
                        <i class="bi bi-clock me-1"></i>Appointment Schedule
                    </h6>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="date" class="form-control" id="appointmentDate" name="appointmentDate"
                                       min="<%= todayStr %>"
                                       value="<%= enteredDate != null ? enteredDate : todayStr %>" required>
                                <label for="appointmentDate">Appointment Date <span class="text-danger">*</span></label>
                            </div>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="time" class="form-control" id="appointmentTime" name="appointmentTime"
                                       value="<%= enteredTime != null ? enteredTime : "10:00" %>" required>
                                <label for="appointmentTime">Appointment Time <span class="text-danger">*</span></label>
                            </div>
                        </div>
                    </div>

                    <!-- Form Submission -->
                    <div class="d-flex justify-content-end gap-3 pt-3 border-top">
                        <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary px-4">Cancel</a>
                        <button type="submit" class="btn btn-primary-gradient px-4 py-2 fw-semibold shadow-sm">
                            <i class="bi bi-check-circle me-1"></i>Confirm & Register Appointment
                        </button>
                    </div>

                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
