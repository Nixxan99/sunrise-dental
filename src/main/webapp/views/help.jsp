<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
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
    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .help-card {
            border: none;
            border-radius: 0.75rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.06);
        }
        .step-badge {
            width: 32px;
            height: 32px;
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
                    <a class="nav-link active fw-medium" href="<%= request.getContextPath() %>/views/help.jsp">
                        <i class="bi bi-question-circle me-1"></i>User Guide / Help
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
        <div class="col-12 col-lg-10">

            <div class="d-flex align-items-center justify-content-between mb-4">
                <div>
                    <h2 class="fw-bold text-dark mb-1">System User Guide & Operational Help</h2>
                    <p class="text-muted mb-0">Step-by-step instructions for clinic staff navigating the Sunrise Dental System</p>
                </div>
                <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left me-1"></i>Dashboard
                </a>
            </div>

            <!-- Guide Section 1: Staff Authentication -->
            <div class="card help-card bg-white p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">1</span>
                    <h4 class="fw-bold mb-0 text-dark">Staff Authentication & Access Control</h4>
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

            <!-- Guide Section 2: Booking an Appointment -->
            <div class="card help-card bg-white p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">2</span>
                    <h4 class="fw-bold mb-0 text-dark">Booking a Patient Appointment</h4>
                </div>
                <p class="text-secondary">
                    To register a new dental appointment for a patient:
                </p>
                <ul>
                    <li>Click <strong>New Appointment</strong> from the navigation bar or dashboard.</li>
                    <li>Enter the patient's full name, address, and Sri Lankan contact number (e.g. <code>0712345678</code> or <code>+94771234567</code>).</li>
                    <li>Select the assigned <strong>Dentist</strong> and the desired <strong>Treatment Procedure</strong>. Standard procedure fees are populated automatically.</li>
                    <li>Select the <strong>Date</strong> (must be today or a future date) and <strong>Time</strong>.</li>
                    <li>Click <strong>Confirm & Register Appointment</strong>. Upon successful registration, the unique appointment number is generated.</li>
                </ul>
            </div>

            <!-- Guide Section 3: Searching & Viewing Records -->
            <div class="card help-card bg-white p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">3</span>
                    <h4 class="fw-bold mb-0 text-dark">Searching & Looking Up Appointments</h4>
                </div>
                <p class="text-secondary">
                    To lookup past or upcoming appointments:
                </p>
                <ul>
                    <li>Click <strong>Search Records</strong> in the navigation header.</li>
                    <li>Enter the numerical appointment ID (e.g. <code>1</code> or <code>101</code>) and submit.</li>
                    <li>The full clinical card will display patient demographics, attending doctor, procedure details, and financial summary.</li>
                </ul>
            </div>

            <!-- Guide Section 4: Billing & Invoice Generation -->
            <div class="card help-card bg-white p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-primary text-white me-3">4</span>
                    <h4 class="fw-bold mb-0 text-dark">Billing & Receipt Generation</h4>
                </div>
                <p class="text-secondary">
                    When a treatment consultation concludes:
                </p>
                <ul>
                    <li>Open the appointment view or click <strong>Invoice</strong> directly from the dashboard table.</li>
                    <li>The system computes the total billing fee: <code>Total = Treatment Cost + Consultation Fee</code>.</li>
                    <li>Verify or adjust consultation fees if necessary, select payment status (<code>PAID</code> or <code>PENDING</code>), and click <strong>Confirm & Issue Final Bill</strong>.</li>
                    <li>Use the <strong>Print Dental Invoice</strong> button to generate a clean, print-formatted tax invoice or thermal receipt.</li>
                </ul>
            </div>

            <!-- Guide Section 5: Distributed RESTful Web Service -->
            <div class="card help-card bg-white p-4 mb-4">
                <div class="d-flex align-items-center mb-3">
                    <span class="step-badge bg-info text-white me-3">5</span>
                    <h4 class="fw-bold mb-0 text-dark">Distributed Web Service Integration (Task B)</h4>
                </div>
                <p class="text-secondary">
                    External healthcare systems and mobile client apps can consume appointment data via RESTful JSON endpoints:
                </p>
                <div class="table-responsive">
                    <table class="table table-bordered table-sm">
                        <thead class="table-light">
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
</body>
</html>
