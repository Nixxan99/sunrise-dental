# Sunrise Dental Clinic Management System (CIS6003)

An enterprise-grade web application developed for managing daily operations, appointments, patient records, treatment workflows, and invoicing at **Sunrise Dental Clinic**.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Architecture & Design Patterns](#architecture--design-patterns)
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

- **User Authentication & Authorization**: Secure role-based management (Admin, Receptionist, Dentist) with salted and hashed passwords.
- **Patient Management**: Complete CRUD operations for patient demographics and contact profiles.
- **Dentist & Schedule Management**: Dentist profiles, specializations, and availability tracking.
- **Appointment Scheduling**: Conflict-free scheduling with support for join views linking patients, dentists, and treatments.
- **Treatment Catalog**: Service and procedure definitions with standardized fee schedules.
- **Invoicing & Billing**: Automated consultation and procedure fee aggregation with payment status reconciliation (`PAID`, `PENDING`, `UNPAID`).

---

## 🏛️ Architecture & Design Patterns

The project adheres to senior software engineering best practices and architectural patterns:

1. **Singleton Pattern**:
   - Implemented in `com.sunrisedental.util.DBConnection` using the **Double-Checked Locking (DCL)** pattern with `volatile` instance visibility. Ensures efficient, thread-safe, lazy-initialized connection management across multi-threaded web requests.
2. **Domain Model / POJO Pattern**:
   - Clean, encapsulated model classes (`User`, `Patient`, `Dentist`, `Treatment`, `Appointment`, `Bill`) implementing `Serializable` with constructors, accessors, `equals()`, `hashCode()`, and `toString()`.
3. **Model-View-Controller (MVC)**:
   - Clear separation between domain models, presentation layer (JSP / REST endpoints), and controller servlets / JAX-RS resources.
4. **Data Access Object (DAO) Pattern** *(planned/in-progress)*:
   - Encapsulates database query logic away from business services and controllers.

---

## 🛠️ Technology Stack

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 17 (LTS) | Modern Java features, strong typing, and performance |
| **Server Framework** | Jakarta Servlets 6.1 / JSP | Dynamic web presentation and controller pipeline |
| **REST API Engine** | Eclipse Jersey 4.0 (JAX-RS) | RESTful API endpoints with Jackson JSON serialization |
| **Database** | MySQL 8.x | Relational storage engine with ACID transactional support |
| **Database Driver** | MySQL Connector/J 8.3.0 | Modern JDBC Type 4 driver |
| **Build & Dependency** | Apache Maven 3.9+ | Build management, dependency resolution, packaging |
| **Continuous Integration** | GitHub Actions | Automated build, test, and package on push and PR |
| **Testing** | JUnit Jupiter 5.13.2 | Automated unit and integration testing |

---

## 🌿 Branching Strategy

The repository follows a standardized Git flow tailored for agile team delivery:

```text
main  ──────────────────────────────────────────● (Production / Stable Releases)
         \                                    /
dev       ●───────●──────────●──────────────●───  (Integration & QA)
                   \        /  \            /
feature/*           ●──────●    ●──────────●     (Individual Feature branches)
```

- **`main`**:
  - The production-ready branch. Only merges from `dev` via approved Pull Requests with green CI checks.
- **`dev`**:
  - The primary integration branch where completed features are integrated and tested.
- **`feature/*`** (e.g., `feature/patient-crud`, `feature/appointment-booking`):
  - Created from `dev` for isolated development of specific requirements. Once completed, a PR is opened targeting `dev`.
- **`hotfix/*`**:
  - Critical patches branching directly from `main` and back-merged to both `main` and `dev`.

---

## 🚀 CI/CD Automation

Continuous Integration is powered by **GitHub Actions** (`.github/workflows/maven-build.yml`):
- Triggers on `push` and `pull_request` against `main` and `dev` branches.
- Sets up Eclipse Temurin JDK 17 with Maven caching.
- Executes:
  ```bash
  ./mvnw clean test package
  ```
- Ensures non-compiling or regression-inducing code is flagged prior to merging.

---

## 💾 Database Configuration

The system connects to MySQL via `com.sunrisedental.util.DBConnection`. Settings can be customized in [`src/main/resources/db.properties`](src/main/resources/db.properties) or via system environment variables:

```properties
db.url=jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=
```

---

## 🏁 Getting Started

### Prerequisites
- **JDK 17** or higher installed and added to `PATH`
- **MySQL 8.x** running locally or remotely
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

## 📂 Project Structure

```text
sunrise-dental-system/
├── .github/
│   └── workflows/
│       └── maven-build.yml          # GitHub Actions CI pipeline
├── .gitignore                       # Ignored build artifacts & IDE files
├── pom.xml                          # Maven build descriptors & dependencies
├── README.md                        # Project documentation
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── com/sunrisedental/
    │   │   │   ├── model/           # Domain POJOs (User, Patient, Dentist, etc.)
    │   │   │   └── util/            # Utilities (DBConnection Singleton)
    │   │   └── org/rav/...          # JAX-RS Application & endpoints
    │   └── resources/
    │       ├── db.properties        # Database connection properties
    │       └── META-INF/beans.xml   # CDI descriptor
    └── test/                        # Unit tests
```

---

## 📜 License & Academic Integrity

This project is developed for the **CIS6003** module. All intellectual property guidelines and academic integrity codes apply.
