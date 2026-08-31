<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Bill" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    Bill bill = (Bill) request.getAttribute("bill");
    Appointment appt = (Appointment) request.getAttribute("appointment");
    Boolean isDraft = (Boolean) request.getAttribute("isDraft");
    String receiptText = (String) request.getAttribute("receiptText");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    int apptNo = (appt != null) ? appt.getAppointmentNumber() : (bill != null ? bill.getAppointmentNumber() : 0);
    double consultationFee = (bill != null) ? bill.getConsultationFee() : 1500.00;
    double treatmentCost = (bill != null) ? bill.getTreatmentCost() : (appt != null ? appt.getCost() : 3000.00);
    double totalAmount = (bill != null) ? bill.getTotalAmount() : (consultationFee + treatmentCost);
    String status = (bill != null && bill.getPaymentStatus() != null) ? bill.getPaymentStatus() : "PAID";
    String issuedAt = (bill != null && bill.getIssuedAt() != null)
            ? bill.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invoice #<%= apptNo %> - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .invoice-card {
            border: none;
            border-radius: 0.75rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.06);
            background-color: #fff;
        }
        .receipt-preview {
            background-color: #f8f9fa;
            border: 1px dashed #ced4da;
            font-family: 'Courier New', Courier, monospace;
            padding: 1.25rem;
            border-radius: 0.5rem;
            white-space: pre-wrap;
        }
        @media print {
            body {
                background-color: #fff !important;
            }
            .no-print {
                display: none !important;
            }
            .invoice-card {
                box-shadow: none !important;
                border: 1px solid #dee2e6 !important;
            }
            .container {
                width: 100% !important;
                max-width: 100% !important;
                padding: 0 !important;
            }
        }
    </style>
</head>
<body>

<!-- Navigation Bar (Hidden during printing) -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm no-print">
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
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/reports">
                        <i class="bi bi-graph-up me-1"></i>Reports
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

    <!-- Action / Navigation Toolbar -->
    <div class="d-flex justify-content-between align-items-center mb-4 no-print">
        <a href="<%= request.getContextPath() %>/appointments?action=view&appointmentNumber=<%= apptNo %>" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Back to Appointment
        </a>
        <div class="d-flex gap-2">
            <button onclick="window.print()" class="btn btn-primary fw-semibold shadow-sm">
                <i class="bi bi-printer me-1"></i>Print Dental Invoice
            </button>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-dark">
                <i class="bi bi-house me-1"></i>Dashboard
            </a>
        </div>
    </div>

    <!-- Feedback Alerts -->
    <% if (successMessage != null) { %>
        <div class="alert alert-success alert-dismissible fade show shadow-sm no-print mb-4" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <%= successMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="alert alert-danger alert-dismissible fade show shadow-sm no-print mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Invoice Sheet Document -->
    <div class="row justify-content-center">
        <div class="col-12 col-lg-9">
            <div class="card invoice-card p-4 p-md-5">

                <!-- Clinic Header -->
                <div class="row pb-4 mb-4 border-bottom align-items-center">
                    <div class="col-sm-7">
                        <div class="d-flex align-items-center mb-2">
                            <div class="bg-primary text-white p-2 rounded-circle me-2 d-flex align-items-center justify-content-center" style="width: 45px; height: 45px;">
                                <i class="bi bi-hospital fs-4"></i>
                            </div>
                            <h3 class="fw-bold text-dark mb-0">Sunrise Dental Clinic</h3>
                        </div>
                        <p class="text-muted small mb-0">
                            123 Healthway Boulevard, Suite 400<br>
                            Colombo, Sri Lanka | Phone: +94 11 234 5678<br>
                            Email: contact@sunrisedental.lk
                        </p>
                    </div>
                    <div class="col-sm-5 text-sm-end mt-3 mt-sm-0">
                        <h4 class="text-uppercase fw-bold text-primary mb-1">
                            <%= (isDraft != null && isDraft) ? "Billing Draft" : "Tax Invoice" %>
                        </h4>
                        <div class="fw-semibold text-secondary">Inv #: INV-<%= apptNo %></div>
                        <div class="text-muted small">Date: <%= issuedAt %></div>
                        <div class="mt-2">
                            <span class="badge <%= "PAID".equalsIgnoreCase(status) ? "bg-success" : "bg-warning text-dark" %> fs-6">
                                <i class="bi <%= "PAID".equalsIgnoreCase(status) ? "bi-check-circle" : "bi-clock" %> me-1"></i><%= status %>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Patient & Appointment Meta -->
                <div class="row mb-4">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <h6 class="text-muted text-uppercase fw-bold small mb-2">Billed To:</h6>
                        <div class="fs-5 fw-bold text-dark"><%= (appt != null && appt.getPatientName() != null) ? appt.getPatientName() : "Valued Patient" %></div>
                        <div class="text-muted small">Appointment Ref: #<%= apptNo %></div>
                    </div>
                    <div class="col-sm-6 text-sm-end">
                        <h6 class="text-muted text-uppercase fw-bold small mb-2">Consultant Doctor:</h6>
                        <div class="fs-6 fw-bold text-dark"><%= (appt != null && appt.getDentistName() != null) ? appt.getDentistName() : "Assigned Dentist" %></div>
                        <div class="text-muted small">Dental Specialist</div>
                    </div>
                </div>

                <!-- Fee Breakdown Table -->
                <div class="table-responsive mb-4">
                    <table class="table table-bordered align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Item / Clinical Service Description</th>
                                <th class="text-center" style="width: 120px;">Qty</th>
                                <th class="text-end" style="width: 180px;">Amount ($)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <div class="fw-semibold">Dental Procedure / Treatment Fee</div>
                                    <small class="text-muted"><%= (appt != null && appt.getTreatmentName() != null) ? appt.getTreatmentName() : "General Dental Treatment" %></small>
                                </td>
                                <td class="text-center">1</td>
                                <td class="text-end fw-semibold">$<%= String.format("%.2f", treatmentCost) %></td>
                            </tr>
                            <tr>
                                <td>
                                    <div class="fw-semibold">Doctor Clinical Consultation Fee</div>
                                    <small class="text-muted">Clinical diagnostic examination & consultation</small>
                                </td>
                                <td class="text-center">1</td>
                                <td class="text-end fw-semibold">$<%= String.format("%.2f", consultationFee) %></td>
                            </tr>
                        </tbody>
                        <tfoot>
                            <tr>
                                <th colspan="2" class="text-end text-uppercase fs-6">Subtotal:</th>
                                <td class="text-end fw-semibold">$<%= String.format("%.2f", totalAmount) %></td>
                            </tr>
                            <tr class="table-light">
                                <th colspan="2" class="text-end text-uppercase fs-5 text-primary">Total Amount Due:</th>
                                <td class="text-end fw-bold fs-5 text-primary">$<%= String.format("%.2f", totalAmount) %></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <!-- Draft Submission Form (If not yet finalized) -->
                <% if (isDraft != null && isDraft) { %>
                <div class="card bg-light border-0 p-3 mb-4 no-print">
                    <h6 class="fw-bold text-dark mb-3"><i class="bi bi-pencil-square me-1"></i>Finalize & Process Invoice</h6>
                    <form action="<%= request.getContextPath() %>/billing" method="post" class="row g-3">
                        <input type="hidden" name="appointmentNumber" value="<%= apptNo %>">
                        <div class="col-md-4">
                            <label class="form-label small fw-medium">Treatment Fee ($)</label>
                            <input type="number" step="0.01" min="0" class="form-control form-control-sm" name="treatmentCost" value="<%= treatmentCost %>" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-medium">Consultation Fee ($)</label>
                            <input type="number" step="0.01" min="0" class="form-control form-control-sm" name="consultationFee" value="<%= consultationFee %>" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-medium">Payment Status</label>
                            <select class="form-select form-select-sm" name="paymentStatus">
                                <option value="PAID" <%= "PAID".equalsIgnoreCase(status) ? "selected" : "" %>>PAID</option>
                                <option value="PENDING" <%= "PENDING".equalsIgnoreCase(status) ? "selected" : "" %>>PENDING</option>
                            </select>
                        </div>
                        <div class="col-12 text-end mt-3">
                            <button type="submit" class="btn btn-success fw-semibold shadow-sm">
                                <i class="bi bi-check2-circle me-1"></i>Confirm & Issue Final Bill
                            </button>
                        </div>
                    </form>
                </div>
                <% } %>

                <!-- Plaintext Receipt Block (For thermal printers or external records) -->
                <% if (receiptText != null && !receiptText.trim().isEmpty()) { %>
                <div class="mb-4">
                    <h6 class="text-muted text-uppercase fw-bold small mb-2"><i class="bi bi-receipt me-1"></i>Thermal Printer Text Receipt</h6>
                    <div class="receipt-preview"><%= receiptText %></div>
                </div>
                <% } %>

                <!-- Invoice Footer -->
                <div class="text-center pt-4 border-top text-muted small">
                    <p class="mb-1">Thank you for visiting Sunrise Dental Clinic!</p>
                    <p class="mb-0">For queries regarding this invoice, please call our billing desk at +94 11 234 5678.</p>
                </div>

            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
