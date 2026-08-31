<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Admin";
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    Treatment editTreatment = (Treatment) request.getAttribute("editTreatment");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successParam = request.getParameter("success");
    String errorParam = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Treatment Management - Sunrise Dental Clinic</title>
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
                        <i class="bi bi-graph-up me-1"></i>Reports & Analytics
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle active fw-semibold" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-gear me-1"></i>Administration
                    </a>
                    <ul class="dropdown-menu shadow-sm">
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/users"><i class="bi bi-people me-2"></i>Manage Staff Accounts</a></li>
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/dentists"><i class="bi bi-person-badge me-2"></i>Manage Dentists</a></li>
                        <li><a class="dropdown-item active fw-semibold" href="<%= request.getContextPath() %>/admin/treatments"><i class="bi bi-clipboard2-pulse me-2"></i>Manage Treatments</a></li>
                    </ul>
                </li>
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
                    <small class="badge bg-danger">ADMIN</small>
                </div>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm fw-semibold">Logout</a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">

    <!-- Page Header & Action Button -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">
                <i class="bi bi-clipboard2-pulse text-primary me-2"></i>Treatment Catalog & Pricing
            </h2>
            <p class="text-muted mb-0">Maintain clinic dental procedures, services, and baseline standard fees.</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2">
            <button class="btn btn-primary-gradient fw-semibold shadow-sm" data-bs-toggle="modal" data-bs-target="#addTreatmentModal">
                <i class="bi bi-plus-circle me-1"></i>Add New Treatment
            </button>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Dashboard
            </a>
        </div>
    </div>

    <!-- Alert Notifications -->
    <% if ("created".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>New dental treatment added successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if ("updated".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>Treatment service and fee updated successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if ("deleted".equals(successParam)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>Treatment removed from active catalog.
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
            <i class="bi bi-exclamation-triangle-fill me-2"></i>Cannot delete treatment. Existing appointments or billing records are associated with this service.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Edit Treatment Inline Card (If Editing) -->
    <% if (editTreatment != null) { %>
    <div class="card clinic-card border-primary border-2 p-4 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="fw-bold text-primary mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Treatment (ID #<%= editTreatment.getTreatmentId() %>)</h5>
            <a href="<%= request.getContextPath() %>/admin/treatments" class="btn btn-outline-secondary btn-sm">Cancel Edit</a>
        </div>
        <form action="<%= request.getContextPath() %>/admin/treatments" method="post" class="row g-3">
            <input type="hidden" name="treatmentId" value="<%= editTreatment.getTreatmentId() %>">
            <div class="col-md-7">
                <div class="form-floating">
                    <input type="text" class="form-control" id="editTreatmentName" name="treatmentName" value="<%= editTreatment.getTreatmentName() %>" required>
                    <label for="editTreatmentName">Treatment Name / Procedure Description <span class="text-danger">*</span></label>
                </div>
            </div>
            <div class="col-md-5">
                <div class="form-floating">
                    <input type="number" step="0.01" min="0" class="form-control" id="editStandardFee" name="standardFee" value="<%= editTreatment.getStandardFee() %>" required>
                    <label for="editStandardFee">Standard Fee ($) <span class="text-danger">*</span></label>
                </div>
            </div>
            <div class="col-12 text-end mt-3">
                <button type="submit" class="btn btn-primary-gradient fw-semibold shadow-sm">
                    <i class="bi bi-check2-circle me-1"></i>Save Changes
                </button>
            </div>
        </form>
    </div>
    <% } %>

    <!-- Treatments Table Card -->
    <div class="card clinic-card p-4 overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle table-clinic mb-0">
                <thead>
                    <tr>
                        <th class="ps-3" style="width: 100px;">ID</th>
                        <th>Treatment / Procedure Name</th>
                        <th class="text-end" style="width: 200px;">Standard Fee ($)</th>
                        <th class="text-end pe-3" style="width: 180px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (treatments != null && !treatments.isEmpty()) {
                        for (Treatment t : treatments) { %>
                            <tr>
                                <td class="ps-3 fw-bold text-muted">#<%= t.getTreatmentId() %></td>
                                <td>
                                    <div class="fw-bold"><%= t.getTreatmentName() %></div>
                                </td>
                                <td class="text-end">
                                    <span class="fw-bold text-success fs-6">$<%= String.format("%.2f", t.getStandardFee()) %></span>
                                </td>
                                <td class="text-end pe-3">
                                    <a href="<%= request.getContextPath() %>/admin/treatments?action=edit&id=<%= t.getTreatmentId() %>" class="btn btn-outline-primary btn-sm me-1" title="Edit Treatment">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <a href="<%= request.getContextPath() %>/admin/treatments?action=delete&id=<%= t.getTreatmentId() %>" class="btn btn-outline-danger btn-sm" onclick="return confirm('Are you sure you want to remove <%= t.getTreatmentName() %>?');\" title="Delete Treatment">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </td>
                            </tr>
                        <% }
                    } else { %>
                        <tr>
                            <td colspan="4" class="text-center py-4 text-muted">
                                <i class="bi bi-inbox fs-3 d-block mb-2"></i>
                                No treatment records found. Click "Add New Treatment" to register one.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal: Add New Treatment -->
<div class="modal fade" id="addTreatmentModal" tabindex="-1" aria-labelledby="addTreatmentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/treatments" method="post">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold" id="addTreatmentModalLabel">
                        <i class="bi bi-clipboard2-plus me-2"></i>Add New Treatment
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="modalTreatmentName" name="treatmentName" placeholder="Treatment Name" required>
                        <label for="modalTreatmentName">Treatment / Procedure Name <span class="text-danger">*</span></label>
                    </div>
                    <div class="form-floating mb-3">
                        <input type="number" step="0.01" min="0" class="form-control" id="modalFee" name="standardFee" placeholder="Standard Fee" required>
                        <label for="modalFee">Standard Fee ($) <span class="text-danger">*</span></label>
                    </div>
                </div>
                <div class="modal-footer bg-body-tertiary">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-gradient fw-semibold shadow-sm">
                        <i class="bi bi-plus-circle me-1"></i>Add Treatment
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
