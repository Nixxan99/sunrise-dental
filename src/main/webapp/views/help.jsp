<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Guide & Help - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Elevated UI Design System -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/theme.css">
    <style>
        .step-badge {
            width: 36px;
            height: 36px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            font-weight: 700;
        }
    </style>
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
                    <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/views/help.jsp">
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
    <div class="row justify-content-center">
        <div class="col-12 col-lg-10">

            <div class="d-flex align-items-center justify-content-between mb-4">
                <div>
                    <h2 class="fw-bold mb-1">System User Guide & Operational Help</h2>
                    <p class="text-muted mb-0">Step-by-step instructions for clinic staff navigating the Sunrise Dental System</p>
                </div>
                <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left me-1"></i>Dashboard
                </a>
            </div>

            <!-- Guide Section 1: Staff Authentication -->
            <div class="card clinic-card p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">1</span>
                    <h4 class="fw-bold mb-0">Staff Authentication & Access Control</h4>
                </div>
                <p class="text-secondary">
                    The system implements strict role-based access control. All operational modules require staff authentication:
                </p>
                <ul>
                    <li>Navigate to <code>/login</code> and input your assigned credentials (e.g. <code>admin / admin123</code> or <code>staff / staff123</code>).</li>
                    <li>The system creates a secure <code>HttpSession</code> that safeguards endpoints via <code>AuthFilter</code>.</li>
                    <li>To exit securely at the end of your shift, click the <strong>Logout</strong> button in the top navigation bar.</li>
                </ul>
            </div>

            <!-- Guide Section 2: Booking an Appointment & Notification -->
            <div class="card clinic-card p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">2</span>
                    <h4 class="fw-bold mb-0">Booking a Patient Appointment & Automated Notifications</h4>
                </div>
                <p class="text-secondary">
                    To register a new dental appointment for a patient:
                </p>
                <ul>
                    <li>Click <strong>New Appointment</strong> from the navigation bar or dashboard.</li>
                    <li>Enter the patient's full name, address, and Sri Lankan contact number (e.g. <code>0712345678</code> or <code>+94771234567</code>).</li>
                    <li>Optionally provide an email address to receive asynchronous Gmail SMTP notifications.</li>
                    <li>Select the assigned <strong>Dentist</strong> and the desired <strong>Treatment Procedure</strong>. Standard procedure fees are populated automatically.</li>
                    <li>Select the <strong>Date</strong> (must be today or a future date) and <strong>Time</strong>.</li>
                    <li>Click <strong>Confirm & Register Appointment</strong>. Upon registration, the system triggers the <code>NotificationService</code> (Observer/Strategy pattern) and <code>EmailNotificationService</code> to dispatch confirmation alerts.</li>
                </ul>
            </div>

            <!-- Guide Section 3: Searching & Viewing Records -->
            <div class="card clinic-card p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">3</span>
                    <h4 class="fw-bold mb-0">Searching & Looking Up Appointments</h4>
                </div>
                <p class="text-secondary">
                    To lookup past or upcoming appointments:
                </p>
                <ul>
                    <li>Click <strong>Search Records</strong> in the navigation header.</li>
                    <li>Enter the numerical appointment ID (e.g. <code>1</code> or <code>101</code>) and submit.</li>
                    <li>The full clinical card will display patient demographics, attending doctor, procedure details, and SMS delivery status.</li>
                </ul>
            </div>

            <!-- Guide Section 4: Billing & Invoice Generation -->
            <div class="card clinic-card p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">4</span>
                    <h4 class="fw-bold mb-0">Billing & AI-Assisted Care Plans</h4>
                </div>
                <p class="text-secondary">
                    When a treatment consultation concludes:
                </p>
                <ul>
                    <li>Open the appointment view or click <strong>Invoice</strong> directly from the dashboard table.</li>
                    <li>The system computes the total billing fee: <code>Total = Treatment Cost + Consultation Fee</code>.</li>
                    <li>Google Gemini AI generates tailored post-treatment recovery guidelines for the patient.</li>
                    <li>Verify or adjust consultation fees if necessary, select payment status (<code>PAID</code> or <code>PENDING</code>), and click <strong>Confirm & Issue Final Bill</strong>.</li>
                    <li>Use the <strong>Print Dental Invoice</strong> button to generate a clean, print-formatted tax invoice or thermal receipt.</li>
                </ul>
            </div>

            <!-- Guide Section 5: Clinic Analytics & Decision-Making Reports -->
            <div class="card clinic-card p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-success text-white me-3">5</span>
                    <h4 class="fw-bold mb-0">Clinic Management Analytics & Reports (Task B)</h4>
                </div>
                <p class="text-secondary">
                    To access executive decision-making metrics:
                </p>
                <ul>
                    <li>Click <strong>Reports & Analytics</strong> in the navigation bar or visit <code>/reports</code>.</li>
                    <li>Interactive <strong>Chart.js</strong> visualizations display revenue generated by dental procedure and doctor workload distribution.</li>
                    <li>Tabular breakdowns show booking counts, average fees, and doctor revenue contributions.</li>
                    <li>Click <strong>Print Management Report</strong> to generate formatted paper/PDF reports for clinic management.</li>
                </ul>
            </div>

            <!-- Guide Section 6: Distributed RESTful Web Service -->
            <div class="card clinic-card p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-info text-white me-3">6</span>
                    <h4 class="fw-bold mb-0">Distributed Web Service Integration (Task B)</h4>
                </div>
                <p class="text-secondary">
                    External healthcare systems and mobile client apps can consume appointment data via RESTful JSON endpoints:
                </p>
                <div class="table-responsive">
                    <table class="table table-bordered table-sm table-clinic">
                        <thead>
                            <tr>
                                <th>HTTP Method</th>
                                <th>Endpoint URL</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><span class="badge bg-success">GET</span></td>
                                <td><code>/api/appointments</code></td>
                                <td>Retrieves all scheduled appointments in JSON format.</td>
                            </tr>
                            <tr>
                                <td><span class="badge bg-success">GET</span></td>
                                <td><code>/api/appointments/{id}</code></td>
                                <td>Retrieves specific appointment details by appointment ID.</td>
                            </tr>
                            <tr>
                                <td><span class="badge bg-primary">POST</span></td>
                                <td><code>/api/appointments</code></td>
                                <td>Accepts JSON payload to book an appointment remotely.</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
