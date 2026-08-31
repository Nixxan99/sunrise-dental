<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sunrisedental.dao.ReportDAO" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    List<ReportDAO.TreatmentReportItem> treatmentReports = (List<ReportDAO.TreatmentReportItem>) request.getAttribute("treatmentReports");
    List<ReportDAO.DoctorReportItem> doctorReports = (List<ReportDAO.DoctorReportItem>) request.getAttribute("doctorReports");
    Map<String, Object> summary = (Map<String, Object>) request.getAttribute("summary");
    Map<String, Object> patientAnalytics = (Map<String, Object>) request.getAttribute("patientAnalytics");

    int totalAppointments = summary != null && summary.get("totalAppointments") != null ? (Integer) summary.get("totalAppointments") : 0;
    int scheduledAppointments = summary != null && summary.get("scheduledAppointments") != null ? (Integer) summary.get("scheduledAppointments") : 0;
    int completedAppointments = summary != null && summary.get("completedAppointments") != null ? (Integer) summary.get("completedAppointments") : 0;
    int totalPatients = summary != null && summary.get("totalPatients") != null ? (Integer) summary.get("totalPatients") : 0;
    int totalDentists = summary != null && summary.get("totalDentists") != null ? (Integer) summary.get("totalDentists") : 0;
    double totalRevenue = summary != null && summary.get("totalRevenue") != null ? (Double) summary.get("totalRevenue") : 0.0;
    double avgInvoice = summary != null && summary.get("avgInvoiceValue") != null ? (Double) summary.get("avgInvoiceValue") : 0.0;

    int totalUniquePatients = patientAnalytics != null && patientAnalytics.get("totalUniquePatients") != null ? (Integer) patientAnalytics.get("totalUniquePatients") : 0;
    int repeatPatients = patientAnalytics != null && patientAnalytics.get("repeatPatients") != null ? (Integer) patientAnalytics.get("repeatPatients") : 0;
    int newPatientsThisMonth = patientAnalytics != null && patientAnalytics.get("newPatientsThisMonth") != null ? (Integer) patientAnalytics.get("newPatientsThisMonth") : 0;
    double repeatRate = patientAnalytics != null && patientAnalytics.get("repeatRate") != null ? (Double) patientAnalytics.get("repeatRate") : 0.0;

    String treatmentLabelsJson = (String) request.getAttribute("treatmentLabelsJson");
    String treatmentRevenuesJson = (String) request.getAttribute("treatmentRevenuesJson");
    String treatmentCountsJson = (String) request.getAttribute("treatmentCountsJson");
    String doctorLabelsJson = (String) request.getAttribute("doctorLabelsJson");
    String doctorCountsJson = (String) request.getAttribute("doctorCountsJson");
    String doctorRevenuesJson = (String) request.getAttribute("doctorRevenuesJson");
    String retentionLabelsJson = (String) request.getAttribute("retentionLabelsJson");
    String retentionCountsJson = (String) request.getAttribute("retentionCountsJson");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clinic Analytics & Reports - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Elevated UI Design System -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/theme.css">
    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        @media print {
            .no-print {
                display: none !important;
            }
            .container-fluid {
                width: 100% !important;
                padding: 0 !important;
            }
        }
    </style>
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark navbar-clinic sticky-top no-print">
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
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/appointments?action=search">
                        <i class="bi bi-search me-1"></i>Search Records
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/reports">
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

<!-- Main Page Body -->
<div class="container-fluid px-4 py-4">

    <!-- Page Header & Print Button -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Clinic Management Analytics & Reports</h2>
            <p class="text-muted mb-0">Strategic decision-making reports, patient retention analytics, and doctor workloads (CIS6003 Task B)</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2 no-print">
            <button onclick="window.print()" class="btn btn-primary-gradient shadow-sm fw-semibold">
                <i class="bi bi-printer me-1"></i>Print Management Report
            </button>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary shadow-sm">
                <i class="bi bi-house me-1"></i>Dashboard
            </a>
        </div>
    </div>

    <!-- Executive Summary Widgets -->
    <div class="row g-3 mb-4">
        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card clinic-card p-4 border-start border-primary border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Total Appointments</div>
                        <div class="fs-2 fw-bold"><%= totalAppointments %></div>
                        <small class="text-muted"><%= scheduledAppointments %> Scheduled | <%= completedAppointments %> Done</small>
                    </div>
                    <div class="badge-subtle-primary p-3 rounded-circle">
                        <i class="bi bi-calendar-check fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card clinic-card p-4 border-start border-success border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Total Clinic Revenue</div>
                        <div class="fs-2 fw-bold text-success">$<%= String.format("%.2f", totalRevenue) %></div>
                        <small class="text-muted">Avg Invoice: $<%= String.format("%.2f", avgInvoice) %></small>
                    </div>
                    <div class="badge-subtle-success p-3 rounded-circle">
                        <i class="bi bi-cash-stack fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card clinic-card p-4 border-start border-info border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Repeat Patient Rate</div>
                        <div class="fs-2 fw-bold text-info"><%= String.format("%.1f", repeatRate) %>%</div>
                        <small class="text-muted"><%= repeatPatients %> repeat of <%= totalUniquePatients %> patients</small>
                    </div>
                    <div class="badge-subtle-primary p-3 rounded-circle">
                        <i class="bi bi-arrow-repeat fs-3 text-info"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
            <div class="card clinic-card p-4 border-start border-warning border-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">New Patients (This Month)</div>
                        <div class="fs-2 fw-bold text-warning"><%= newPatientsThisMonth %></div>
                        <small class="text-muted">Registered in current month</small>
                    </div>
                    <div class="badge-subtle-warning p-3 rounded-circle">
                        <i class="bi bi-person-plus fs-3"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Charts Row -->
    <div class="row g-4 mb-4">
        <!-- Chart 1: Revenue by Treatment -->
        <div class="col-12 col-xl-5">
            <div class="card clinic-card p-4 h-100">
                <h5 class="fw-bold mb-3">
                    <i class="bi bi-bar-chart-fill me-2 text-primary"></i>Revenue by Dental Procedure ($)
                </h5>
                <div>
                    <canvas id="treatmentRevenueChart" style="max-height: 280px;"></canvas>
                </div>
            </div>
        </div>

        <!-- Chart 2: Doctor Workload Breakdown -->
        <div class="col-12 col-md-6 col-xl-4">
            <div class="card clinic-card p-4 h-100">
                <h5 class="fw-bold mb-3">
                    <i class="bi bi-pie-chart-fill me-2 text-success"></i>Doctor Appointments
                </h5>
                <div>
                    <canvas id="doctorWorkloadChart" style="max-height: 280px;"></canvas>
                </div>
            </div>
        </div>

        <!-- Chart 3: Patient Retention Donut -->
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card clinic-card p-4 h-100">
                <h5 class="fw-bold mb-3">
                    <i class="bi bi-people-fill me-2 text-info"></i>Patient Retention
                </h5>
                <div>
                    <canvas id="retentionChart" style="max-height: 280px;"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Printable Summary Tables Section -->
    <div class="row g-4">
        <!-- Table 1: Treatment Revenue Breakdown -->
        <div class="col-12 col-lg-7">
            <div class="card clinic-card overflow-hidden">
                <div class="card-header bg-transparent py-3 border-bottom px-4">
                    <h5 class="mb-0 fw-bold">
                        <i class="bi bi-table me-2 text-primary"></i>Treatment Procedure Performance Breakdown
                    </h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle table-clinic mb-0">
                            <thead>
                                <tr>
                                    <th class="ps-4">Treatment / Procedure</th>
                                    <th class="text-center">Bookings</th>
                                    <th class="text-end">Standard Fee</th>
                                    <th class="text-end pe-4">Total Earned ($)</th>
                                </tr>
                            </thead>
                            <tbody>
                            <% if (treatmentReports != null && !treatmentReports.isEmpty()) {
                                for (ReportDAO.TreatmentReportItem item : treatmentReports) {
                            %>
                                <tr>
                                    <td class="ps-4 fw-semibold"><%= item.getTreatmentName() %></td>
                                    <td class="text-center"><span class="badge bg-body-secondary text-body border"><%= item.getAppointmentCount() %></span></td>
                                    <td class="text-end">$<%= String.format("%.2f", item.getAverageFee()) %></td>
                                    <td class="text-end pe-4 fw-bold text-success">$<%= String.format("%.2f", item.getTotalRevenue()) %></td>
                                </tr>
                            <%  }
                               } else { %>
                                <tr>
                                    <td colspan="4" class="text-center py-3 text-muted">No treatment data recorded.</td>
                                </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Table 2: Doctor Workload Summary -->
        <div class="col-12 col-lg-5">
            <div class="card clinic-card overflow-hidden">
                <div class="card-header bg-transparent py-3 border-bottom px-4">
                    <h5 class="mb-0 fw-bold">
                        <i class="bi bi-person-check-fill me-2 text-success"></i>Doctor Activity Summary
                    </h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle table-clinic mb-0">
                            <thead>
                                <tr>
                                    <th class="ps-4">Doctor</th>
                                    <th class="text-center">Appointments</th>
                                    <th class="text-end pe-4">Revenue ($)</th>
                                </tr>
                            </thead>
                            <tbody>
                            <% if (doctorReports != null && !doctorReports.isEmpty()) {
                                for (ReportDAO.DoctorReportItem doc : doctorReports) {
                            %>
                                <tr>
                                    <td class="ps-4">
                                        <div class="fw-semibold"><%= doc.getDoctorName() %></div>
                                        <small class="text-muted"><%= doc.getSpecialization() %></small>
                                    </td>
                                    <td class="text-center"><span class="badge bg-primary"><%= doc.getAppointmentCount() %></span></td>
                                    <td class="text-end pe-4 fw-bold text-success">$<%= String.format("%.2f", doc.getTotalRevenue()) %></td>
                                </tr>
                            <%  }
                               } else { %>
                                <tr>
                                    <td colspan="3" class="text-center py-3 text-muted">No doctor data recorded.</td>
                                </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<!-- Chart.js Render Script -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        // Chart 1: Bar Chart for Treatment Revenue
        const treatmentLabels = <%= treatmentLabelsJson != null ? treatmentLabelsJson : "[]" %>;
        const treatmentRevenues = <%= treatmentRevenuesJson != null ? treatmentRevenuesJson : "[]" %>;

        const ctxTreatment = document.getElementById('treatmentRevenueChart').getContext('2d');
        new Chart(ctxTreatment, {
            type: 'bar',
            data: {
                labels: treatmentLabels,
                datasets: [{
                    label: 'Total Revenue ($)',
                    data: treatmentRevenues,
                    backgroundColor: 'rgba(13, 110, 253, 0.75)',
                    borderColor: 'rgba(13, 110, 253, 1)',
                    borderWidth: 1,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) { return '$' + value; }
                        }
                    }
                }
            }
        });

        // Chart 2: Doughnut Chart for Doctor Workload
        const doctorLabels = <%= doctorLabelsJson != null ? doctorLabelsJson : "[]" %>;
        const doctorCounts = <%= doctorCountsJson != null ? doctorCountsJson : "[]" %>;

        const ctxDoctor = document.getElementById('doctorWorkloadChart').getContext('2d');
        new Chart(ctxDoctor, {
            type: 'doughnut',
            data: {
                labels: doctorLabels,
                datasets: [{
                    data: doctorCounts,
                    backgroundColor: [
                        '#0d6efd',
                        '#198754',
                        '#0dcaf0',
                        '#ffc107',
                        '#6f42c1',
                        '#fd7e14'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });

        // Chart 3: Doughnut Chart for Patient Retention
        const retentionLabels = <%= retentionLabelsJson != null ? retentionLabelsJson : "[]" %>;
        const retentionCounts = <%= retentionCountsJson != null ? retentionCountsJson : "[]" %>;

        const ctxRetention = document.getElementById('retentionChart').getContext('2d');
        new Chart(ctxRetention, {
            type: 'doughnut',
            data: {
                labels: retentionLabels,
                datasets: [{
                    data: retentionCounts,
                    backgroundColor: [
                        '#0dcaf0',
                        '#198754'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
