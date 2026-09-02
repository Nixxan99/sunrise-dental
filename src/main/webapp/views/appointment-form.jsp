<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="java.time.LocalDate" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String staffName = currentUser != null ? currentUser.getFullName() : "Staff";
    String role = currentUser != null ? currentUser.getRole() : "STAFF";
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    List<Patient> allPatients = (List<Patient>) request.getAttribute("allPatients");

    Object selectedPatientIdObj = request.getAttribute("selectedPatientId");
    String selectedPatientId = selectedPatientIdObj != null ? String.valueOf(selectedPatientIdObj) : "";

    String patientName = (String) request.getAttribute("enteredPatientName");
    String nic = (String) request.getAttribute("enteredNic");
    String address = (String) request.getAttribute("enteredAddress");
    String contactNumber = (String) request.getAttribute("enteredContactNumber");
    String email = (String) request.getAttribute("enteredEmail");
    String enteredDentistId = (String) request.getAttribute("enteredDentistId");
    String enteredTreatmentId = (String) request.getAttribute("enteredTreatmentId");
    String enteredDate = (String) request.getAttribute("enteredDate");
    String enteredTime = (String) request.getAttribute("enteredTime");

    String todayStr = LocalDate.now().toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Appointment - Sunrise Dental Clinic</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Elevated UI Design System -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/theme.css">
    <style>
        .patient-search-results {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            z-index: 1050;
            max-height: 250px;
            overflow-y: auto;
            background: var(--bs-body-bg);
            border: 1px solid var(--bs-border-color);
            border-radius: 0.375rem;
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
        }
        .search-item {
            padding: 0.6rem 1rem;
            cursor: pointer;
            border-bottom: 1px solid var(--bs-border-color-translucent);
        }
        .search-item:hover {
            background-color: var(--bs-secondary-bg);
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
                    <a class="nav-link text-white-50" href="<%= request.getContextPath() %>/patients">
                        <i class="bi bi-people-fill me-1"></i>Patients
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/appointments?action=new">
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
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-sm fw-semibold">Logout</a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-9">

            <div class="d-flex align-items-center justify-content-between mb-4">
                <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
                </a>
                <span class="text-muted small"><i class="bi bi-info-circle me-1"></i>Fields marked with <span class="text-danger">*</span> are mandatory</span>
            </div>

            <div class="card clinic-card p-4 p-md-5 mb-4">
                <div class="d-flex align-items-center gap-3 pb-3 mb-4 border-bottom">
                    <div class="badge-subtle-primary p-3 rounded-circle">
                        <i class="bi bi-calendar2-plus fs-3"></i>
                    </div>
                    <div>
                        <h3 class="fw-bold mb-0">Book Patient Appointment</h3>
                        <p class="text-muted small mb-0">Register appointment scheduling, patient demographics, NIC, and assigned clinical practitioner.</p>
                    </div>
                </div>

                <!-- Error Alert -->
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <%= request.getAttribute("errorMessage") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <!-- Universal Search Autocomplete Box -->
                <div class="card bg-body-tertiary p-3 mb-4 border-0 position-relative">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <label for="patientSearchInput" class="form-label fw-bold text-primary small mb-0">
                            <i class="bi bi-search me-1"></i>Live Patient Universal Search (Name, NIC, Phone, ID)
                        </label>
                        <button type="button" class="btn btn-link btn-sm text-decoration-none p-0" onclick="clearPatientForm()">
                            <i class="bi bi-person-plus me-1"></i>+ Clear / New Patient
                        </button>
                    </div>
                    <div class="input-group">
                        <span class="input-group-text bg-body"><i class="bi bi-person-lines-fill text-muted"></i></span>
                        <input type="text" class="form-control" id="patientSearchInput"
                               placeholder="Type name, NIC (e.g. 1990... or 85...V), phone (077...), or patient ID..."
                               autocomplete="off">
                    </div>
                    <div id="searchResultsDropdown" class="patient-search-results d-none"></div>
                </div>

                <form action="<%= request.getContextPath() %>/appointments" method="post" id="appointmentForm">
                    <input type="hidden" name="patientId" id="patientId" value="<%= selectedPatientId %>">

                    <!-- Section: Patient Demographics -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h6 class="text-primary text-uppercase fw-bold mb-0">
                            <i class="bi bi-person-badge me-1"></i>Patient Identification & Details
                        </h6>
                        <span id="patientStatusBadge" class="badge <%= (selectedPatientId != null && !selectedPatientId.isEmpty()) ? "bg-info" : "bg-success" %>">
                            <%= (selectedPatientId != null && !selectedPatientId.isEmpty()) ? "Existing Patient Record #" + selectedPatientId : "New Patient Registration" %>
                        </span>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="patientName" name="patientName"
                                       placeholder="Patient Full Name"
                                       value="<%= patientName != null ? patientName : "" %>" required>
                                <label for="patientName">Patient Full Name <span class="text-danger">*</span></label>
                            </div>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="nic" name="nic"
                                       placeholder="National ID (NIC)"
                                       value="<%= nic != null ? nic : "" %>" required>
                                <label for="nic">National ID Card (NIC) <span class="text-danger">*</span></label>
                            </div>
                            <small class="text-muted ms-1">e.g. 199012345678 (12-digit) or 853451234V (9-digit+V/X)</small>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="contactNumber" name="contactNumber"
                                       placeholder="Contact Number"
                                       value="<%= contactNumber != null ? contactNumber : "" %>" required>
                                <label for="contactNumber">Contact Number <span class="text-danger">*</span></label>
                            </div>
                            <small class="text-muted ms-1">Format: 07XXXXXXXX or +947XXXXXXXX</small>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="email" class="form-control" id="email" name="email"
                                       placeholder="Email Address"
                                       value="<%= email != null ? email : "" %>">
                                <label for="email">Email Address (For Invoices & Receipts)</label>
                            </div>
                        </div>

                        <div class="col-12">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="address" name="address"
                                       placeholder="Residential Address"
                                       value="<%= address != null ? address : "" %>">
                                <label for="address">Residential Address / City</label>
                            </div>
                        </div>
                    </div>

                    <!-- Section: Clinical Consultation Details -->
                    <h6 class="text-primary text-uppercase fw-bold mb-3">
                        <i class="bi bi-heart-pulse me-1"></i>Consultation & Procedure
                    </h6>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <select class="form-select" id="dentistId" name="dentistId" required>
                                    <option value="">-- Choose Assigned Doctor --</option>
                                    <% if (dentists != null) {
                                        for (Dentist d : dentists) {
                                            boolean selected = enteredDentistId != null && enteredDentistId.equals(String.valueOf(d.getDentistId()));
                                    %>
                                        <option value="<%= d.getDentistId() %>" <%= selected ? "selected" : "" %>>
                                            <%= d.getName() %> (<%= d.getSpecialization() %>)
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                                <label for="dentistId">Attending Dentist <span class="text-danger">*</span></label>
                            </div>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <select class="form-select" id="treatmentId" name="treatmentId" required>
                                    <option value="">-- Choose Dental Treatment --</option>
                                    <% if (treatments != null) {
                                        for (Treatment t : treatments) {
                                            boolean selected = enteredTreatmentId != null && enteredTreatmentId.equals(String.valueOf(t.getTreatmentId()));
                                    %>
                                        <option value="<%= t.getTreatmentId() %>" data-fee="<%= t.getStandardFee() %>" <%= selected ? "selected" : "" %>>
                                            <%= t.getTreatmentName() %> - Standard Fee: LKR <%= String.format("%.2f", t.getStandardFee()) %>
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                                <label for="treatmentId">Dental Treatment Procedure <span class="text-danger">*</span></label>
                            </div>
                        </div>
                    </div>

                    <!-- Section: Schedule Details -->
                    <h6 class="text-primary text-uppercase fw-bold mb-3">
                        <i class="bi bi-clock me-1"></i>Appointment Schedule
                    </h6>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="date" class="form-control" id="appointmentDate" name="appointmentDate"
                                       min="<%= todayStr %>"
                                       value="<%= enteredDate != null ? enteredDate : todayStr %>" required>
                                <label for="appointmentDate">Appointment Date <span class="text-danger">*</span></label>
                            </div>
                        </div>

                        <div class="col-12 col-md-6">
                            <div class="form-floating">
                                <input type="time" class="form-control" id="appointmentTime" name="appointmentTime"
                                       value="<%= enteredTime != null ? enteredTime : "10:00" %>" required>
                                <label for="appointmentTime">Appointment Time <span class="text-danger">*</span></label>
                            </div>
                        </div>
                    </div>

                    <!-- Form Submission -->
                    <div class="d-flex justify-content-end gap-3 pt-3 border-top">
                        <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-outline-secondary px-4">Cancel</a>
                        <button type="submit" class="btn btn-primary-gradient px-4 py-2 fw-semibold shadow-sm">
                            <i class="bi bi-check-circle me-1"></i>Confirm & Register Appointment
                        </button>
                    </div>

                </form>
            </div>
        </div>
    </div>
</div>

<script>
    const contextPath = '<%= request.getContextPath() %>';
    const searchInput = document.getElementById('patientSearchInput');
    const resultsDropdown = document.getElementById('searchResultsDropdown');
    const patientStatusBadge = document.getElementById('patientStatusBadge');
    let debounceTimer = null;

    if (searchInput) {
        searchInput.addEventListener('input', function () {
            const q = this.value.trim();
            clearTimeout(debounceTimer);
            if (q.length === 0) {
                resultsDropdown.classList.add('d-none');
                resultsDropdown.innerHTML = '';
                return;
            }

            debounceTimer = setTimeout(() => {
                fetch(contextPath + '/api/patients/search?q=' + encodeURIComponent(q))
                    .then(response => response.json())
                    .then(data => {
                        resultsDropdown.innerHTML = '';
                        const patients = data.data || [];
                        if (patients.length === 0) {
                            resultsDropdown.innerHTML = '<div class="p-3 text-muted small"><i class="bi bi-info-circle me-1"></i>No matching registered patients found. Fill details below to register as new.</div>';
                        } else {
                            patients.forEach(p => {
                                const item = document.createElement('div');
                                item.className = 'search-item';
                                const nicText = p.nic ? (' | NIC: ' + p.nic) : '';
                                const emailText = p.email ? (' | ' + p.email) : '';
                                item.innerHTML = '<strong>' + escapeHtml(p.fullName) + '</strong> <small class=\"text-muted\">(#' + p.patientId + ')</small><br>' +
                                    '<small class=\"text-secondary\"><i class=\"bi bi-telephone me-1\"></i>' + escapeHtml(p.contactNumber || 'N/A') + nicText + emailText + '</small>';
                                item.addEventListener('click', () => selectPatient(p));
                                resultsDropdown.appendChild(item);
                            });
                        }
                        resultsDropdown.classList.remove('d-none');
                    })
                    .catch(() => {
                        resultsDropdown.classList.add('d-none');
                    });
            }, 250);
        });

        document.addEventListener('click', function (e) {
            if (!searchInput.contains(e.target) && !resultsDropdown.contains(e.target)) {
                resultsDropdown.classList.add('d-none');
            }
        });
    }

    function selectPatient(patient) {
        document.getElementById('patientId').value = patient.patientId || '';
        document.getElementById('patientName').value = patient.fullName || '';
        document.getElementById('nic').value = patient.nic || '';
        document.getElementById('contactNumber').value = patient.contactNumber || '';
        document.getElementById('email').value = patient.email || '';
        document.getElementById('address').value = patient.address || '';
        searchInput.value = patient.fullName + (patient.nic ? ' (' + patient.nic + ')' : '');
        resultsDropdown.classList.add('d-none');

        if (patientStatusBadge) {
            patientStatusBadge.className = 'badge bg-info';
            patientStatusBadge.textContent = 'Existing Patient Record #' + patient.patientId;
        }
    }

    function clearPatientForm() {
        document.getElementById('patientId').value = '';
        document.getElementById('patientName').value = '';
        document.getElementById('nic').value = '';
        document.getElementById('contactNumber').value = '';
        document.getElementById('email').value = '';
        document.getElementById('address').value = '';
        if (searchInput) searchInput.value = '';
        if (resultsDropdown) resultsDropdown.classList.add('d-none');

        if (patientStatusBadge) {
            patientStatusBadge.className = 'badge bg-success';
            patientStatusBadge.textContent = 'New Patient Registration';
        }
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/theme-switcher.js"></script>
</body>
</html>
