<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff Member";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
    int totalAppts = request.getAttribute("totalAppointments") != null ? (Integer) request.getAttribute("totalAppointments") : 0;
    long scheduledCount = request.getAttribute("scheduledCount") != null ? (Long) request.getAttribute("scheduledCount") : 0;
    long completedCount = request.getAttribute("completedCount") != null ? (Long) request.getAttribute("completedCount") : 0;
    long todayCount = request.getAttribute("todayCount") != null ? (Long) request.getAttribute("todayCount") : 0;
    double revenue = request.getAttribute("totalRevenue") != null ? (Double) request.getAttribute("totalRevenue") : 0.0;
    int dentistsCount = request.getAttribute("totalDentists") != null ? (Integer) request.getAttribute("totalDentists") : 0;
    int treatmentsCount = request.getAttribute("totalTreatments") != null ? (Integer) request.getAttribute("totalTreatments") : 0;
    String message = request.getParameter("message");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .stat-card {
            border: none;
            border-radius: 0.75rem;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .stat-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }
        .navbar-brand {
            font-weight: 700;
            letter-spacing: -0.5px;
        }
        .table-card {
            border: none;
            border-radius: 0.75rem;
            box-shadow: 0 4px 15px rgba(0,0,0,0.04);
        }
    </style>
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm">
    <div class="container-fluid px-4">
        <a class="navbar-brand d-flex align-items-center" href="<%= request.getContextPath() %>/dashboard">
            <i class="bi bi-hospital fs-3 me-2"></i>
            <span>Sunrise Dental Clinic</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-dismiss="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-3">
                <li class="nav-item">
                    <a class="nav-link active fw-medium" href="<%= request.getContextPath() %>/dashboard">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
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
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/admin/users">
                        <i class="bi bi-people-fill me-1"></i>User Management
                    </a>
                </li>
                <% } %>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/views/help.jsp">
                        <i class="bi bi-question-circle me-1"></i>Help
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/api/appointments" target="_blank">
                        <i class="bi bi-code-slash me-1"></i>REST API
                    </a>
                </li>
            </ul>

            <div class="d-flex align-items-center text-white">
                <div class="me-3 text-end d-none d-md-block">
                    <div class="fw-semibold"><%= staffName %></div>
                    <small class="badge bg-light text-primary"><%= role %></small>
                </div>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </div>
</nav>

<!-- Main Container -->
<div class="container-fluid px-4 py-4">

    <% if ("password_changed".equals(message)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm mb-4" role="alert">
            <i class="bi bi-shield-check me-2"></i>
            <strong>Password Updated:</strong> Your security credentials have been updated successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Page Header & Quick Actions -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold text-dark mb-1">Clinic Operations Dashboard</h2>
            <p class="text-muted mb-0">Overview of patient appointments, dental consultations, and revenue</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2">
            <a href="<%= request.getContextPath() %>/appointments?action=new" class="btn btn-primary shadow-sm">
                <i class="bi bi-plus-circle me-1"></i>Book Appointment
            </a>
            <a href="<%= request.getContextPath() %>/reports" class="btn btn-outline-primary shadow-sm">
                <i class="bi bi-graph-up me-1"></i>View Reports
            </a>
            <a href="<%= request.getContextPath() %>/appointments?action=search" class="btn btn-outline-secondary shadow-sm">
                <i class="bi bi-search me-1"></i>Find Appointment
            </a>
        </div>
    </div>

    <!-- Metric Cards Row -->
    <div class="row g-3 mb-4">
        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card stat-card shadow-sm bg-white p-3 border-start border-primary border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Total Appointments</div>
                        <div class="fs-3 fw-bold text-dark"><%= totalAppts %></div>
                    </div>
                    <div class="bg-primary bg-opacity-10 text-primary p-3 rounded-circle">
                        <i class="bi bi-calendar-check fs-4"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card stat-card shadow-sm bg-white p-3 border-start border-warning border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Scheduled / Pending</div>
                        <div class="fs-3 fw-bold text-warning"><%= scheduledCount %></div>
                    </div>
                    <div class="bg-warning bg-opacity-10 text-warning p-3 rounded-circle">
                        <i class="bi bi-clock-history fs-4"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card stat-card shadow-sm bg-white p-3 border-start border-success border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Completed / Paid</div>
                        <div class="fs-3 fw-bold text-success"><%= completedCount %></div>
                    </div>
                    <div class="bg-success bg-opacity-10 text-success p-3 rounded-circle">
                        <i class="bi bi-check2-circle fs-4"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card stat-card shadow-sm bg-white p-3 border-start border-info border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Total Revenue</div>
                        <div class="fs-3 fw-bold text-info">$<%= String.format("%.2f", revenue) %></div>
                    </div>
                    <div class="bg-info bg-opacity-10 text-info p-3 rounded-circle">
                        <i class="bi bi-cash-stack fs-4"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Appointments Data Table Card -->
    <div class="card table-card bg-white">
        <div class="card-header bg-white py-3 border-bottom d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold text-dark">
                <i class="bi bi-list-task me-2 text-primary"></i>Recent Clinic Appointments
            </h5>
            <span class="badge bg-secondary"><%= appointments != null ? appointments.size() : 0 %> records</span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-4">Appt #</th>
                            <th>Patient Name</th>
                            <th>Dentist</th>
                            <th>Treatment Procedure</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Fee</th>
                            <th>Status</th>
                            <th class="text-end pe-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (appointments != null && !appointments.isEmpty()) {
                        for (Appointment a : appointments) {
                    %>
                        <tr>
                            <td class="ps-4 fw-bold text-primary">#<%= a.getAppointmentNumber() %></td>
                            <td class="fw-semibold"><%= a.getPatientName() != null ? a.getPatientName() : "Patient #" + a.getPatientId() %></td>
                            <td><i class="bi bi-person-badge text-muted me-1"></i><%= a.getDentistName() != null ? a.getDentistName() : "Doctor #" + a.getDentistId() %></td>
                            <td><span class="badge bg-light text-dark border"><%= a.getTreatmentName() != null ? a.getTreatmentName() : "Treatment #" + a.getTreatmentId() %></span></td>
                            <td><%= a.getAppointmentDate() %></td>
                            <td><%= a.getAppointmentTime() %></td>
                            <td class="fw-semibold">$<%= String.format("%.2f", a.getCost()) %></td>
                            <td>
                                <% if ("COMPLETED".equalsIgnoreCase(a.getStatus())) { %>
                                    <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>COMPLETED</span>
                                <% } else if ("CANCELLED".equalsIgnoreCase(a.getStatus())) { %>
                                    <span class="badge bg-danger">CANCELLED</span>
                                <% } else { %>
                                    <span class="badge bg-warning text-dark"><i class="bi bi-clock me-1"></i>SCHEDULED</span>
                                <% } %>
                            </td>
                            <td class="text-end pe-4">
                                <a href="<%= request.getContextPath() %>/appointments?action=view&appointmentNumber=<%= a.getAppointmentNumber() %>"
                                   class="btn btn-sm btn-outline-primary me-1" title="View Full Details">
                                    <i class="bi bi-eye"></i> View
                                </a>
                                <a href="<%= request.getContextPath() %>/billing?appointmentNumber=<%= a.getAppointmentNumber() %>"
                                   class="btn btn-sm btn-outline-success" title="View / Generate Bill">
                                    <i class="bi bi-receipt"></i> Invoice
                                </a>
                            </td>
                        </tr>
                    <%  }
                       } else { %>
                        <tr>
                            <td colspan="9" class="text-center py-5 text-muted">
                                <i class="bi bi-calendar-x fs-1 d-block mb-2 text-secondary"></i>
                                No appointments registered in the system yet.
                                <div class="mt-2">
                                    <a href="<%= request.getContextPath() %>/appointments?action=new" class="btn btn-primary btn-sm">
                                        <i class="bi bi-plus-circle me-1"></i>Register First Appointment
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
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
