<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.time.LocalDate" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");

    String patientName = (String) request.getAttribute("enteredPatientName");
    String address = (String) request.getAttribute("enteredAddress");
    String contactNumber = (String) request.getAttribute("enteredContactNumber");
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
    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .form-card {
            border: none;
            border-radius: 0.75rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.06);
        }
    </style>
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm">
    <div class="container-fluid px-4">
        <a class="navbar-brand d-flex align-items-center fw-bold" href="<%= request.getContextPath() %>/dashboard">
            <i class="bi bi-hospital fs-3 me-2"></i>
            <span>Sunrise Dental Clinic</span>
        </a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-3">
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/dashboard">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-medium" href="<%= request.getContextPath() %>/appointments?action=new">
                        <i class="bi bi-calendar-plus me-1"></i>New Appointment
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/appointments?action=search">
                        <i class="bi bi-search me-1"></i>Search Records
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/views/help.jsp">
                        <i class="bi bi-question-circle me-1"></i>Help
                    </a>
                </li>
            </ul>
            <div class="d-flex align-items-center text-white">
                <span class="me-3 small text-white-50"><i class="bi bi-person-circle me-1"></i><%= staffName %></span>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm">Logout</a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-8">

            <div class="d-flex align-items-center justify-content-between mb-3">
                <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
                </a>
                <span class="text-muted small"><i class="bi bi-info-circle me-1"></i>All fields marked * are required</span>
            </div>

            <div class="card form-card bg-white overflow-hidden">
                <div class="card-header bg-primary text-white p-3 px-4">
                    <h4 class="mb-0 fw-semibold">
                        <i class="bi bi-calendar2-plus me-2"></i>Book Patient Appointment
                    </h4>
                </div>

                <div class="card-body p-4 p-md-5">

                    <!-- Error Alert -->
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <%= request.getAttribute("errorMessage") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/appointments" method="post" id="appointmentForm">

                        <!-- Section: Patient Demographics -->
                        <h6 class="text-primary text-uppercase fw-bold mb-3 border-bottom pb-2">
                            <i class="bi bi-person-badge me-1"></i>Patient Details
                        </h6>

                        <div class="row g-3 mb-4">
                            <div class="col-12 col-md-6">
                                <label for="patientName" class="form-label fw-medium">Patient Full Name <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="patientName" name="patientName"
                                       placeholder="e.g. Kamal Perera"
                                       value="<%= patientName != null ? patientName : "" %>" required>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="contactNumber" class="form-label fw-medium">Contact Number <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="contactNumber" name="contactNumber"
                                       placeholder="e.g. 0712345678 or +94771234567"
                                       value="<%= contactNumber != null ? contactNumber : "" %>" required>
                                <small class="text-muted">Format: Sri Lankan mobile (07XXXXXXXX or +947XXXXXXXX)</small>
                            </div>

                            <div class="col-12">
                                <label for="address" class="form-label fw-medium">Residential Address</label>
                                <input type="text" class="form-control" id="address" name="address"
                                       placeholder="e.g. 45 Galle Road, Colombo 03"
                                       value="<%= address != null ? address : "" %>">
                            </div>
                        </div>

                        <!-- Section: Clinical Consultation Details -->
                        <h6 class="text-primary text-uppercase fw-bold mb-3 border-bottom pb-2">
                            <i class="bi bi-heart-pulse me-1"></i>Consultation & Procedure
                        </h6>

                        <div class="row g-3 mb-4">
                            <div class="col-12 col-md-6">
                                <label for="dentistId" class="form-label fw-medium">Select Dentist <span class="text-danger">*</span></label>
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
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="treatmentId" class="form-label fw-medium">Select Treatment Procedure <span class="text-danger">*</span></label>
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
                            </div>
                        </div>

                        <!-- Section: Schedule Details -->
                        <h6 class="text-primary text-uppercase fw-bold mb-3 border-bottom pb-2">
                            <i class="bi bi-clock me-1"></i>Appointment Schedule
                        </h6>

                        <div class="row g-3 mb-4">
                            <div class="col-12 col-md-6">
                                <label for="appointmentDate" class="form-label fw-medium">Appointment Date <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="appointmentDate" name="appointmentDate"
                                       min="<%= todayStr %>"
                                       value="<%= enteredDate != null ? enteredDate : todayStr %>" required>
                                <small class="text-muted">Dates in the past are not permitted.</small>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="appointmentTime" class="form-label fw-medium">Appointment Time <span class="text-danger">*</span></label>
                                <input type="time" class="form-control" id="appointmentTime" name="appointmentTime"
                                       value="<%= enteredTime != null ? enteredTime : "10:00" %>" required>
                            </div>
                        </div>

                        <!-- Form Submission -->
                        <div class="d-flex justify-content-end gap-2 pt-3 border-top">
                            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-light px-4">Cancel</a>
                            <button type="submit" class="btn btn-primary px-4 fw-semibold shadow-sm">
                                <i class="bi bi-check-circle me-1"></i>Confirm & Register Appointment
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
