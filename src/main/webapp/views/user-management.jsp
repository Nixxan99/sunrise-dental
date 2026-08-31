<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Administrator";
    String role = currentUser != null ? currentUser.getRole() : "ADMIN";
    List<User> users = (List<User>) request.getAttribute("users");

    String success = request.getParameter("success");
    String error = request.getParameter("error");
    String tempPass = request.getParameter("temp");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff User Management - Sunrise Dental Clinic</title>
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
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/reports">
                        <i class="bi bi-graph-up me-1"></i>Reports & Analytics
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle active fw-semibold" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-gear me-1"></i>Administration
                    </a>
                    <ul class="dropdown-menu shadow-sm">
                        <li><a class="dropdown-item active fw-semibold" href="<%= request.getContextPath() %>/admin/users"><i class="bi bi-people me-2"></i>Manage Staff Accounts</a></li>
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/dentists"><i class="bi bi-person-badge me-2"></i>Manage Dentists</a></li>
                        <li><a class="dropdown-item" href="<%= request.getContextPath() %>/admin/treatments"><i class="bi bi-clipboard2-pulse me-2"></i>Manage Treatments</a></li>
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

<div class="container-fluid px-4 py-4">

    <!-- Page Header & New User Action Button -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Staff Account & Access Management</h2>
            <p class="text-muted mb-0">Security ethics, role assignments, password compliance, and account provisioning</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2">
            <button type="button" class="btn btn-primary-gradient shadow-sm fw-semibold" data-bs-toggle="modal" data-bs-target="#newUserModal">
                <i class="bi bi-person-plus-fill me-1"></i>Register New Staff Account
            </button>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Dashboard
            </a>
        </div>
    </div>

    <!-- Feedback Alerts -->
    <% if ("created".equals(success)) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <strong>Success:</strong> New clinic staff operator account has been created. They will be prompted to set a new password on their first login.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if ("reset".equals(success)) { %>
        <div class="alert alert-info alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i>
            <strong>Password Reset Completed:</strong> Temporary password set to <code><%= tempPass != null ? tempPass : "DentalStaff123!" %></code>. User must change it upon next login.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if ("deleted".equals(success)) { %>
        <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-trash-fill me-2"></i>
            Staff account successfully removed from system.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if ("self_delete".equals(error)) { %>
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-shield-x me-2"></i>
            Security Violation: You cannot delete your own active administrator account.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Users Table Card -->
    <div class="card clinic-card overflow-hidden">
        <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center px-4">
            <h5 class="mb-0 fw-bold">
                <i class="bi bi-people me-2 text-primary"></i>Clinic Staff Directory
            </h5>
            <span class="badge bg-primary"><%= users != null ? users.size() : 0 %> Operators</span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle table-clinic mb-0">
                    <thead>
                        <tr>
                            <th class="ps-4">UID</th>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Assigned Role</th>
                            <th>Security Compliance</th>
                            <th class="text-end pe-4">Account Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (users != null && !users.isEmpty()) {
                        for (User u : users) {
                    %>
                        <tr>
                            <td class="ps-4 fw-bold text-muted">#<%= u.getUserId() %></td>
                            <td class="fw-semibold"><%= u.getFullName() %></td>
                            <td><code><%= u.getUsername() %></code></td>
                            <td>
                                <% if ("ADMIN".equalsIgnoreCase(u.getRole())) { %>
                                    <span class="badge bg-danger">ADMINISTRATOR</span>
                                <% } else if ("RECEPTIONIST".equalsIgnoreCase(u.getRole())) { %>
                                    <span class="badge bg-primary">RECEPTIONIST</span>
                                <% } else { %>
                                    <span class="badge bg-secondary">STAFF</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (u.isMustChangePassword()) { %>
                                    <span class="badge bg-warning text-dark"><i class="bi bi-exclamation-circle me-1"></i>Must Change Password</span>
                                <% } else { %>
                                    <span class="badge bg-success"><i class="bi bi-shield-check me-1"></i>Password Active</span>
                                <% } %>
                            </td>
                            <td class="text-end pe-4">
                                <!-- Reset Password Button -->
                                <button type="button" class="btn btn-sm btn-outline-warning me-1"
                                        data-bs-toggle="modal" data-bs-target="#resetModal<%= u.getUserId() %>"
                                        title="Reset Password">
                                    <i class="bi bi-key"></i> Reset
                                </button>

                                <!-- Delete Account Button (Disabled for current user) -->
                                <% if (currentUser != null && currentUser.getUserId() == u.getUserId()) { %>
                                    <button class="btn btn-sm btn-outline-secondary" disabled title="Self-deletion prohibited">
                                        <i class="bi bi-lock"></i> Current
                                    </button>
                                <% } else { %>
                                    <button type="button" class="btn btn-sm btn-outline-danger"
                                            data-bs-toggle="modal" data-bs-target="#deleteModal<%= u.getUserId() %>"
                                            title="Delete User Account">
                                        <i class="bi bi-trash"></i> Delete
                                    </button>
                                <% } %>
                            </td>
                        </tr>

                        <!-- Reset Password Modal for this User -->
                        <div class="modal fade" id="resetModal<%= u.getUserId() %>" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow">
                                    <form action="<%= request.getContextPath() %>/admin/users" method="post">
                                        <input type="hidden" name="action" value="reset">
                                        <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                        <div class="modal-header">
                                            <h5 class="modal-title fw-bold">Reset Password for <%= u.getFullName() %></h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <p class="text-muted small">
                                                Assign a temporary password for <strong><%= u.getUsername() %></strong>. The user will be required to change it upon next login.
                                            </p>
                                            <div class="form-floating mb-3">
                                                <input type="text" class="form-control" id="tempPassword<%= u.getUserId() %>" name="tempPassword" value="StaffReset123!" required>
                                                <label for="tempPassword<%= u.getUserId() %>">Temporary Password</label>
                                            </div>
                                        </div>
                                        <div class="modal-footer bg-body-tertiary">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-warning fw-semibold">Confirm Reset</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Delete User Modal for this User -->
                        <div class="modal fade" id="deleteModal<%= u.getUserId() %>" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow">
                                    <form action="<%= request.getContextPath() %>/admin/users" method="post">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                        <div class="modal-header bg-danger text-white">
                                            <h5 class="modal-title fw-bold"><i class="bi bi-exclamation-triangle-fill me-2"></i>Delete User Account</h5>
                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            Are you sure you want to permanently delete the staff account for <strong><%= u.getFullName() %> (<%= u.getUsername() %>)</strong>?
                                        </div>
                                        <div class="modal-footer bg-body-tertiary">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-danger fw-semibold">Delete Account</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                    <%  }
                       } else { %>
                        <tr>
                            <td colspan="6" class="text-center py-4 text-muted">No staff users registered.</td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>

<!-- Modal: Register New Staff Account -->
<div class="modal fade" id="newUserModal" tabindex="-1" aria-labelledby="newUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/users" method="post">
                <input type="hidden" name="action" value="create">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold" id="newUserModalLabel"><i class="bi bi-person-plus-fill me-2"></i>Register New Staff Account</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="fullName" name="fullName" placeholder="Full Name" required>
                        <label for="fullName">Staff Member Full Name <span class="text-danger">*</span></label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="username" name="username" placeholder="Username" required>
                        <label for="username">Login Username <span class="text-danger">*</span></label>
                    </div>

                    <div class="form-floating mb-3">
                        <select class="form-select" id="role" name="role" required>
                            <option value="STAFF" selected>STAFF (General Operator)</option>
                            <option value="RECEPTIONIST">RECEPTIONIST (Front Desk)</option>
                            <option value="ADMIN">ADMIN (System Administrator)</option>
                        </select>
                        <label for="role">System Role <span class="text-danger">*</span></label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="password" class="form-control" id="password" name="password" value="Welcome123!" minlength="6" required>
                        <label for="password">Initial Temporary Password <span class="text-danger">*</span></label>
                    </div>
                    <small class="text-muted d-block">The user will be required to change this password upon first login.</small>
                </div>
                <div class="modal-footer bg-body-tertiary">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-gradient fw-semibold shadow-sm">
                        <i class="bi bi-check-circle me-1"></i>Create Staff Account
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
