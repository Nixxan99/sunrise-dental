<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff Member";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
    Patient editPatient = (Patient) request.getAttribute("editPatient");
    String searchQuery = (String) request.getAttribute("searchQuery");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successParam = request.getParameter("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Directory - Sunrise Dental Clinic</title>
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
                <i class="bi bi-person-badge text-primary me-2"></i>Patient Profiles & Registry
            </h2>
            <p class="text-muted mb-0">Manage persistent patient records, lookup contact info, and track clinical appointment histories.</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2">
            <button type="button" class="btn btn-primary-gradient shadow-sm fw-semibold" data-bs-toggle="modal" data-bs-target="#newPatientModal">
                <i class="bi bi-person-plus-fill me-1"></i>Register New Patient
            </button>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Dashboard
            </a>
        </div>
    </div>

    <!-- Alert Notifications -->
    <% if ("created".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>Patient record successfully registered!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if ("updated".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>Patient demographic details updated successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Edit Patient Card (If Editing) -->
    <% if (editPatient != null) { %>
    <div class="card clinic-card border-primary border-2 p-4 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="fw-bold text-primary mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Patient Profile (ID #<%= editPatient.getPatientId() %>)</h5>
            <a href="<%= request.getContextPath() %>/patients" class="btn btn-outline-secondary btn-sm">Cancel Edit</a>
        </div>
        <form action="<%= request.getContextPath() %>/patients" method="post" class="row g-3">
            <input type="hidden" name="patientId" value="<%= editPatient.getPatientId() %>">
            <div class="col-md-4">
                <div class="form-floating">
                    <input type="text" class="form-control" id="editFullName" name="fullName" value="<%= editPatient.getFullName() %>" required>
                    <label for="editFullName">Patient Full Name <span class="text-danger">*</span></label>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-floating">
                    <input type="tel" class="form-control" id="editContactNumber" name="contactNumber" value="<%= editPatient.getContactNumber() %>" required>
                    <label for="editContactNumber">Contact Number <span class="text-danger">*</span></label>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-floating">
                    <input type="text" class="form-control" id="editAddress" name="address" value="<%= editPatient.getAddress() != null ? editPatient.getAddress() : "" %>">
                    <label for="editAddress">Residential Address / City</label>
                </div>
            </div>
            <div class="col-12 text-end mt-3">
                <button type="submit" class="btn btn-primary-gradient fw-semibold shadow-sm">
                    <i class="bi bi-check2-circle me-1"></i>Save Patient Details
                </button>
            </div>
        </form>
    </div>
    <% } %>

    <!-- Search & Filter Card -->
    <div class="card clinic-card p-3 mb-4">
        <form action="<%= request.getContextPath() %>/patients" method="get" class="row g-2 align-items-center">
            <input type="hidden" name="action" value="list">
            <div class="col-12 col-md-8 col-lg-6">
                <div class="input-group">
                    <span class="input-group-text bg-transparent border-end-0"><i class="bi bi-search text-muted"></i></span>
                    <input type="text" class="form-control border-start-0 ps-0" name="q" placeholder="Search by patient full name or phone number..." value="<%= searchQuery != null ? searchQuery : "" %>">
                    <button class="btn btn-primary-gradient px-4" type="submit">Search</button>
                    <% if (searchQuery != null && !searchQuery.isEmpty()) { %>
                        <a href="<%= request.getContextPath() %>/patients" class="btn btn-outline-secondary" title="Clear search">
                            <i class="bi bi-x-lg"></i>
                        </a>
                    <% } %>
                </div>
            </div>
            <div class="col-12 col-md-4 col-lg-6 text-md-end text-muted small">
                Showing <strong><%= patients != null ? patients.size() : 0 %></strong> patient profiles
            </div>
        </form>
    </div>

    <!-- Patients Directory Table Card -->
    <div class="card clinic-card overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle table-clinic mb-0">
                <thead>
                    <tr>
                        <th class="ps-4" style="width: 100px;">Patient ID</th>
                        <th>Full Name</th>
                        <th>Contact Number</th>
                        <th>Address / City</th>
                        <th class="text-end pe-4" style="width: 320px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                <% if (patients != null && !patients.isEmpty()) {
                    for (Patient p : patients) {
                %>
                    <tr>
                        <td class="ps-4 fw-bold text-muted">#<%= p.getPatientId() %></td>
                        <td>
                            <div class="fw-bold fs-6"><%= p.getFullName() %></div>
                        </td>
                        <td>
                            <span class="badge bg-body-secondary text-body border">
                                <i class="bi bi-telephone me-1 text-primary"></i><%= p.getContactNumber() %>
                            </span>
                        </td>
                        <td class="text-muted"><%= (p.getAddress() != null && !p.getAddress().trim().isEmpty()) ? p.getAddress() : "—" %></td>
                        <td class="text-end pe-4">
                            <a href="<%= request.getContextPath() %>/patients?action=view&id=<%= p.getPatientId() %>" class="btn btn-outline-primary btn-sm me-1" title="View Appointment History & Clinical Timeline">
                                <i class="bi bi-clock-history me-1"></i>History
                            </a>
                            <a href="<%= request.getContextPath() %>/appointments?action=new&patientId=<%= p.getPatientId() %>" class="btn btn-outline-success btn-sm me-1" title="Schedule Appointment for this Patient">
                                <i class="bi bi-calendar-plus me-1"></i>Book
                            </a>
                            <a href="<%= request.getContextPath() %>/patients?action=edit&id=<%= p.getPatientId() %>" class="btn btn-outline-secondary btn-sm" title="Edit Patient Details">
                                <i class="bi bi-pencil"></i>
                            </a>
                        </td>
                    </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="5" class="text-center py-5 text-muted">
                            <i class="bi bi-people fs-1 d-block mb-2 text-secondary"></i>
                            No patient records found<%= (searchQuery != null && !searchQuery.isEmpty()) ? " matching \"" + searchQuery + "\"" : "" %>.
                            <div class="mt-2">
                                <button class="btn btn-primary-gradient btn-sm" data-bs-toggle="modal" data-bs-target="#newPatientModal">
                                    <i class="bi bi-person-plus-fill me-1"></i>Register New Patient
                                </button>
                            </div>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>

</div>

<!-- Modal: Register New Patient -->
<div class="modal fade" id="newPatientModal" tabindex="-1" aria-labelledby="newPatientModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/patients" method="post">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold" id="newPatientModalLabel">
                        <i class="bi bi-person-plus-fill me-2"></i>Register New Patient
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="modalFullName" name="fullName" placeholder="Full Name" required>
                        <label for="modalFullName">Patient Full Name <span class="text-danger">*</span></label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="tel" class="form-control" id="modalContact" name="contactNumber" placeholder="0771234567" required>
                        <label for="modalContact">Contact Phone Number <span class="text-danger">*</span></label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="modalAddress" name="address" placeholder="Address">
                        <label for="modalAddress">Residential Address / City</label>
                    </div>
                </div>
                <div class="modal-footer bg-body-tertiary">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-gradient fw-semibold shadow-sm">
                        <i class="bi bi-check-circle me-1"></i>Register Patient
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
