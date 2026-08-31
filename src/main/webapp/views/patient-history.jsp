<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="com.sunrisedental.model.PatientAppointmentHistoryItem" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff Member";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    Patient patient = (Patient) request.getAttribute("patient");
    List<PatientAppointmentHistoryItem> history = (List<PatientAppointmentHistoryItem>) request.getAttribute("history");

    int totalVisits = history != null ? history.size() : 0;
    double totalBilled = 0.0;
    int completedCount = 0;

    if (history != null) {
        for (PatientAppointmentHistoryItem item : history) {
            if ("PAID".equalsIgnoreCase(item.getPaymentStatus()) || "COMPLETED".equalsIgnoreCase(item.getAppointmentStatus())) {
                totalBilled += item.getTotalAmount();
                completedCount++;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clinical History - <%= patient != null ? patient.getFullName() : "Patient" %> - Sunrise Dental Clinic</title>
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
                    <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/patients">
                        <i class="bi bi-people-fill me-1"></i>Patients
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/appointments?action=new">
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
                <div class="text-end d-none d-md-block">
                    <div class="fw-semibold small"><%= staffName %></div>
                    <small class="badge bg-light text-primary"><%= role %></small>
                </div>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm fw-semibold">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </div>
</nav>

<!-- Main Container -->
<div class="container-fluid px-4 py-4">

    <!-- Page Header & Action Controls -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">
                <i class="bi bi-journal-medical text-primary me-2"></i>Patient Clinical Record & History
            </h2>
            <p class="text-muted mb-0">Demographic profile, lifetime appointment timeline, procedures, and payment histories.</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2">
            <% if (patient != null) { %>
                <a href="<%= request.getContextPath() %>/appointments?action=new&patientId=<%= patient.getPatientId() %>" class="btn btn-primary-gradient shadow-sm fw-semibold">
                    <i class="bi bi-calendar-plus me-1"></i>Schedule New Visit
                </a>
            <% } %>
            <a href="<%= request.getContextPath() %>/patients" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Back to Directory
            </a>
        </div>
    </div>

    <% if (patient != null) { %>
    <!-- Patient Demographic Profile Card -->
    <div class="card clinic-card p-4 mb-4">
        <div class="row g-4 align-items-center">
            <div class="col-12 col-md-4 border-md-end">
                <div class="d-flex align-items-center">
                    <div class="badge-subtle-primary p-3 rounded-circle me-3">
                        <i class="bi bi-person-fill fs-2"></i>
                    </div>
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Patient Profile</div>
                        <h4 class="fw-bold mb-0"><%= patient.getFullName() %></h4>
                        <span class="badge bg-secondary mt-1">ID: #<%= patient.getPatientId() %></span>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-md-4">
                <div class="mb-2">
                    <span class="text-muted small d-block">Contact Phone Number</span>
                    <span class="fw-semibold fs-6">
                        <i class="bi bi-telephone-fill text-primary me-1"></i><%= patient.getContactNumber() %>
                    </span>
                </div>
                <div>
                    <span class="text-muted small d-block">Residential Address / City</span>
                    <span class="fw-semibold text-secondary">
                        <i class="bi bi-geo-alt-fill text-danger me-1"></i><%= (patient.getAddress() != null && !patient.getAddress().trim().isEmpty()) ? patient.getAddress() : "Not Provided" %>
                    </span>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-md-4">
                <div class="row g-2">
                    <div class="col-6">
                        <div class="p-3 bg-body-tertiary rounded text-center">
                            <div class="text-muted small text-uppercase fw-bold">Total Visits</div>
                            <div class="fs-4 fw-bold text-primary"><%= totalVisits %></div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="p-3 bg-body-tertiary rounded text-center">
                            <div class="text-muted small text-uppercase fw-bold">Lifetime Total</div>
                            <div class="fs-4 fw-bold text-success">$<%= String.format("%.2f", totalBilled) %></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Appointment Clinical Timeline Table Card -->
    <div class="card clinic-card overflow-hidden">
        <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center px-4">
            <h5 class="mb-0 fw-bold">
                <i class="bi bi-clock-history me-2 text-primary"></i>Appointment & Treatment Timeline
            </h5>
            <span class="badge bg-primary"><%= totalVisits %> Visits Recorded</span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle table-clinic mb-0">
                    <thead>
                        <tr>
                            <th class="ps-4">Appt #</th>
                            <th>Date & Time</th>
                            <th>Attending Dentist</th>
                            <th>Treatment Received</th>
                            <th>Service Fee</th>
                            <th>Total Bill</th>
                            <th>Status</th>
                            <th>Payment</th>
                            <th class="text-end pe-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (history != null && !history.isEmpty()) {
                        for (PatientAppointmentHistoryItem item : history) {
                    %>
                        <tr>
                            <td class="ps-4 fw-bold text-primary">#<%= item.getAppointmentNumber() %></td>
                            <td>
                                <div class="fw-semibold"><%= item.getAppointmentDate() %></div>
                                <small class="text-muted"><%= item.getAppointmentTime() %></small>
                            </td>
                            <td>
                                <div class="fw-semibold"><i class="bi bi-person-badge text-muted me-1"></i><%= item.getDentistName() %></div>
                                <small class="text-muted"><%= item.getDentistSpecialization() != null ? item.getDentistSpecialization() : "Dental Surgeon" %></small>
                            </td>
                            <td>
                                <span class="badge bg-body-secondary text-body border"><%= item.getTreatmentName() %></span>
                            </td>
                            <td>$<%= String.format("%.2f", item.getTreatmentCost()) %></td>
                            <td class="fw-bold text-dark">$<%= String.format("%.2f", item.getTotalAmount()) %></td>
                            <td>
                                <% if ("COMPLETED".equalsIgnoreCase(item.getAppointmentStatus())) { %>
                                    <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>COMPLETED</span>
                                <% } else if ("CANCELLED".equalsIgnoreCase(item.getAppointmentStatus())) { %>
                                    <span class="badge bg-danger">CANCELLED</span>
                                <% } else { %>
                                    <span class="badge bg-warning text-dark"><i class="bi bi-clock me-1"></i>SCHEDULED</span>
                                <% } %>
                            </td>
                            <td>
                                <% if ("PAID".equalsIgnoreCase(item.getPaymentStatus())) { %>
                                    <span class="badge bg-success"><i class="bi bi-cash-stack me-1"></i>PAID</span>
                                <% } else if ("PENDING".equalsIgnoreCase(item.getPaymentStatus())) { %>
                                    <span class="badge bg-warning text-dark">PENDING</span>
                                <% } else { %>
                                    <span class="badge bg-secondary">UNBILLED</span>
                                <% } %>
                            </td>
                            <td class="text-end pe-4">
                                <a href="<%= request.getContextPath() %>/appointments?action=view&appointmentNumber=<%= item.getAppointmentNumber() %>"
                                   class="btn btn-sm btn-outline-primary me-1" title="View Appointment Details">
                                    <i class="bi bi-eye"></i> View
                                </a>
                                <a href="<%= request.getContextPath() %>/billing?appointmentNumber=<%= item.getAppointmentNumber() %>"
                                   class="btn btn-sm btn-outline-success" title="View / Generate Invoice">
                                    <i class="bi bi-receipt"></i> Invoice
                                </a>
                            </td>
                        </tr>
                    <%  }
                       } else { %>
                        <tr>
                            <td colspan="9" class="text-center py-5 text-muted">
                                <i class="bi bi-calendar-x fs-1 d-block mb-2 text-secondary"></i>
                                No previous appointments or consultations recorded for this patient.
                                <div class="mt-2">
                                    <a href="<%= request.getContextPath() %>/appointments?action=new&patientId=<%= patient.getPatientId() %>" class="btn btn-primary-gradient btn-sm">
                                        <i class="bi bi-calendar-plus me-1"></i>Schedule First Appointment
                                    </a>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <% } else { %>
        <div class="alert alert-warning">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>Patient record could not be loaded. Please return to the <a href="<%= request.getContextPath() %>/patients">Patient Directory</a>.
        </div>
    <% } %>

</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
