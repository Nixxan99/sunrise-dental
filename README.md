# Sunrise Dental Clinic Management System (CIS6003)

An enterprise-grade web application developed for managing daily operations, appointments, patient records, treatment workflows, and invoicing at **Sunrise Dental Clinic**.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Architecture & Design Patterns](#architecture--design-patterns)
- [Cloud-Native Distributed Database Strategy](#cloud-native-distributed-database-strategy)
- [Technology Stack](#technology-stack)
- [Branching Strategy](#branching-strategy)
- [CI/CD Automation](#cicd-automation)
- [Database Configuration](#database-configuration)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [License & Academic Integrity](#license--academic-integrity)

---

## 🏥 Project Overview

The **Sunrise Dental Clinic Management System** is developed as part of the **CIS6003** advanced software development module. The application streamlines clinic administration by providing role-based access control (RBAC) for receptionists, dentists, and clinic administrators. It manages patient registrations, dentist schedules, appointment booking, treatment logs, and billing calculations with high data integrity and security.

---

## ✨ Key Features

- **User Management & Password Ethics**: Role-based access control (`ADMIN`, `RECEPTIONIST`, `STAFF`) with SHA-256 password hashing and mandatory first-login password update policies.
- **Patient Management**: Complete CRUD operations for patient demographics and contact profiles.
- **Dentist & Schedule Management**: Dentist profiles, specializations, and availability tracking.
- **Appointment Scheduling**: Conflict-free scheduling with support for join views linking patients, dentists, and treatments.
- **Automated Patient Notifications**: Strategy pattern dispatching automated SMS and Gmail alerts on booking, logged in `notification_logs`.
- **Application-Tier Auditing**: Observer pattern logging all appointment creation events in `appointment_audit_log` without requiring database triggers.
- **Invoicing & Billing**: Automated consultation and procedure fee aggregation with payment status reconciliation (`PAID`, `PENDING`, `UNPAID`).
- **Clinic Analytics & Reporting**: Interactive Chart.js visualizations (earnings by treatment, doctor workload) with printable summary tables.
- **Distributed RESTful Web Service**: JSON endpoints (`/api/appointments`) for remote client and hospital system integrations.

---

## 🏛️ Architecture & Design Patterns

The project adheres to senior software engineering best practices and architectural patterns:

1. **Singleton Pattern**:
   - Implemented in `com.sunrisedental.util.DBConnection` using the **Double-Checked Locking (DCL)** pattern with `volatile` instance visibility. Ensures efficient, thread-safe, lazy-initialized connection management across multi-threaded web requests.
2. **Data Access Object (DAO) Pattern**:
   - Encapsulates database query logic away from business services and controllers using `PreparedStatement` parameterization to prevent SQL injection (`UserDAO`, `AppointmentDAO`, `PatientDAO`, `DentistDAO`, `TreatmentDAO`, `BillDAO`, `ReportDAO`, `AppointmentAuditDAO`).
3. **Observer Pattern**:
   - Implemented in `com.sunrisedental.service.AuditService` and `AppointmentRegistrationListener`. Decouples appointment insertion from audit log recording, substituting for database triggers on distributed cloud databases.
4. **Strategy Pattern**:
   - Implemented in `com.sunrisedental.service.NotificationService` with interchangeable dispatch strategies (`SmsNotificationService`, `GmailNotificationService`).
5. **Model-View-Controller (MVC)**:
   - Clear separation between domain models, presentation layer (JSP / REST endpoints), and controller servlets.
6. **Pure Domain Service**:
   - Implemented in `com.sunrisedental.service.BillingService` for deterministic business fee calculations and receipt generation.

---

## ☁️ Cloud-Native Distributed Database Strategy

The persistence tier is connected to **TiDB Cloud (Serverless Distributed SQL)**. 

Because distributed SQL engines do not support native database triggers or stored procedures (to avoid distributed locking latency and Raft consensus bottlenecks), business logic and auditing are intentionally shifted to the application tier using design patterns.

> 📄 For full academic analysis and evaluation, see [**docs/ARCHITECTURAL_DECISIONS.md**](docs/ARCHITECTURAL_DECISIONS.md).

---

## 🛠️ Technology Stack

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 17 (LTS) | Modern Java features, strong typing, and performance |
| **Server Framework** | Jakarta Servlets 6.1 / JSP | Dynamic web presentation and controller pipeline |
| **Distributed REST Engine** | Jakarta Servlets + Google Gson 2.10.1 | RESTful JSON API endpoints |
| **Database** | TiDB Cloud Serverless / MySQL 8.x | Distributed relational storage with ACID transactional support |
| **Database Driver** | MySQL Connector/J 8.3.0 | Modern JDBC Type 4 driver |
| **UI Framework** | Bootstrap 5.3.3 + Bootstrap Icons | Responsive clinic operator interface |
| **Data Visualizations** | Chart.js 4.4.1 CDN | Interactive procedure revenue & doctor workload charts |
| **Build & Dependency** | Apache Maven 3.9+ | Build management, dependency resolution, packaging |
| **Continuous Integration** | GitHub Actions | Automated build, test, and package on push and PR |
| **Testing** | JUnit Jupiter 5.13.2 | Automated unit and integration testing |

---

## 🌿 Branching Strategy

The repository follows a standardized Git flow tailored for agile team delivery:

```text
main  ──────────────────────────────────────────────────────────● (Production / Stable Releases)
         \                                    /
dev       ●──────────────●────────────────────●──────────────────● (Integration & QA)
                   \        /  \            /
feature/*           ●──────●    ●──────────● (Individual Feature branches)
```

---

## 🚀 CI/CD Automation

Continuous Integration is powered by **GitHub Actions** (`.github/workflows/maven-build.yml`):
- Triggers on `push` and `pull_request` against `main` and `dev` branches.
- Sets up Eclipse Temurin JDK 17 with Maven caching.
- Executes:
  ```bash
  ./mvnw clean test package
  ```

---

## 💾 Database Configuration

The system connects to MySQL / TiDB via `com.sunrisedental.util.DBConnection`. Settings can be customized in [`src/main/resources/db.properties`](src/main/resources/db.properties):

```properties
db.url=jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/sunrise_dental_db?sslMode=VERIFY_IDENTITY&useSSL=true&serverTimezone=UTC
db.user=42UVtMmfzWtuANk.root
db.password=xn0oZNxqPx9YNeU3
```

---

## 🏁 Getting Started

### Prerequisites
- **JDK 17** or higher installed and added to `PATH`
- **MySQL 8.x** or **TiDB Cloud**
- **Git**

### Installation & Build

1. Clone the repository:
   ```bash
   git clone https://github.com/<your-org>/sunrise-dental-system.git
   cd sunrise-dental-system
   ```

2. Compile and run tests:
   ```bash
   ./mvnw clean test
   ```

3. Package as WAR artifact:
   ```bash
   ./mvnw package
   ```
   The deployable WAR file will be generated under `target/sunrise-dental-system-1.0-SNAPSHOT.war`.

---

## 📜 License & Academic Integrity

This project is developed for the **CIS6003** module. All intellectual property guidelines and academic integrity codes apply.
