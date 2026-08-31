<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Admin";
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    Dentist editDentist = (Dentist) request.getAttribute("editDentist");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successParam = request.getParameter("success");
    String errorParam = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dentist Management - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .dentist-card {
            border: none;
            border-radius: 0.75rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.06);
            background-color: #fff;
        }
        .table th {
            font-weight: 600;
            background-color: #f8f9fa;
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
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-3">
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/dashboard">
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
                        <i class="bi bi-graph-up me-1"></i>Reports
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle active fw-semibold" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-gear me-1"></i>Administration
                    </a>
                    <ul class="dropdown-menu shadow-sm">
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/users"><i class="bi bi-people me-2"></i>Manage Staff Accounts</a></li>
                        <li><a class="dropdown-item active fw-semibold" href="<%= request.getContextPath() %>/admin/dentists"><i class="bi bi-person-badge me-2"></i>Manage Dentists</a></li>
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/treatments"><i class="bi bi-clipboard2-pulse me-2"></i>Manage Treatments</a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/views/help.jsp">
                        <i class="bi bi-question-circle me-1"></i>Help
                    </a>
                </li>
            </ul>
            <div class="d-flex align-items-center text-white">
                <span class="me-3 small text-white-50"><i class="bi bi-person-circle me-1"></i><%= staffName %> (ADMIN)</span>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm">Logout</a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">

    <!-- Page Header & Action Button -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-dark mb-1">
                <i class="bi bi-person-badge text-primary me-2"></i>Dentist Directory
            </h2>
            <p class="text-muted small mb-0">Register, edit, and manage clinic dental practitioners and specializations.</p>
        </div>
        <div class="d-flex gap-2">
            <button class="btn btn-primary fw-semibold shadow-sm" data-bs-toggle="modal" data-bs-target="#addDentistModal">
                <i class="bi bi-plus-circle me-1"></i>Add New Dentist
            </button>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Dashboard
            </a>
        </div>
    </div>

    <!-- Alert Notifications -->
    <% if ("created".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>New dentist registered successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if ("updated".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>Dentist profile updated successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if ("deleted".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>Dentist removed from the system.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if ("cannot_delete".equals(errorParam)) { %>
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>Cannot delete dentist. Existing appointments or records are linked to this doctor.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Edit Dentist Inline Card (If Editing) -->
    <% if (editDentist != null) { %>
    <div class="card dentist-card border-primary border-2 p-4 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="fw-bold text-primary mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Dentist Details (ID #<%= editDentist.getDentistId() %>)</h5>
            <a href="<%= request.getContextPath() %>/admin/dentists" class="btn btn-outline-secondary btn-sm">Cancel Edit</a>
        </div>
        <form action="<%= request.getContextPath() %>/admin/dentists" method="post" class="row g-3">
            <input type="hidden" name="dentistId" value="<%= editDentist.getDentistId() %>">
            <div class="col-md-4">
                <label class="form-label fw-semibold small">Full Name / Doctor Title <span class="text-danger">*</span></label>
                <input type="text" class="form-control" name="name" value="<%= editDentist.getName() %>" required>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold small">Specialization <span class="text-danger">*</span></label>
                <input type="text" class="form-control" name="specialization" value="<%= editDentist.getSpecialization() %>" required>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold small">Contact Number <span class="text-danger">*</span></label>
                <input type="tel" class="form-control" name="contactNumber" value="<%= editDentist.getContactNumber() %>" required>
            </div>
            <div class="col-12 text-end mt-3">
                <button type="submit" class="btn btn-primary fw-semibold shadow-sm">
                    <i class="bi bi-check2-circle me-1"></i>Save Changes
                </button>
            </div>
        </form>
    </div>
    <% } %>

    <!-- Dentists Table Card -->
    <div class="card dentist-card p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px;">ID</th>
                        <th>Dentist Name</th>
                        <th>Specialization</th>
                        <th>Contact Number</th>
                        <th class="text-end" style="width: 180px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (dentists != null && !dentists.isEmpty()) {
                        for (Dentist d : dentists) { %>
                            <tr>
                                <td class="fw-bold text-muted">#<%= d.getDentistId() %></td>
                                <td>
                                    <div class="fw-bold text-dark"><%= d.getName() %></div>
                                </td>
                                <td>
                                    <span class="badge bg-primary bg-opacity-10 text-primary fs-6 fw-normal px-2 py-1">
                                        <%= d.getSpecialization() %>
                                    </span>
                                </td>
                                <td>
                                    <i class="bi bi-telephone text-muted me-1"></i><%= d.getContactNumber() %>
                                </td>
                                <td class="text-end">
                                    <a href="<%= request.getContextPath() %>/admin/dentists?action=edit&id=<%= d.getDentistId() %>" class="btn btn-outline-primary btn-sm me-1" title="Edit Dentist">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <a href="<%= request.getContextPath() %>/admin/dentists?action=delete&id=<%= d.getDentistId() %>" class="btn btn-outline-danger btn-sm" onclick="return confirm('Are you sure you want to remove <%= d.getName() %>?');" title="Delete Dentist">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </td>
                            </tr>
                        <% }
                    } else { %>
                        <tr>
                            <td colspan="5" class="text-center py-4 text-muted">
                                <i class="bi bi-inbox fs-3 d-block mb-2"></i>
                                No dentist records found. Click "Add New Dentist" to register one.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal: Add New Dentist -->
<div class="modal fade" id="addDentistModal" tabindex="-1" aria-labelledby="addDentistModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/dentists" method="post">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold" id="addDentistModalLabel">
                        <i class="bi bi-person-plus me-2"></i>Register New Dentist
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Full Name / Doctor Title <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="name" placeholder="e.g. Dr. Kasun Silva" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Specialization <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="specialization" placeholder="e.g. Endodontics / Orthodontics" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Contact Number <span class="text-danger">*</span></label>
                        <input type="tel" class="form-control" name="contactNumber" placeholder="e.g. 0771234567" required>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary fw-semibold shadow-sm">
                        <i class="bi bi-plus-circle me-1"></i>Register Dentist
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
