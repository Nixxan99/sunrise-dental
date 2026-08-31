-- ============================================================================
-- Database Schema and Seed Data for Sunrise Dental Clinic Management System
-- CIS6003 Advanced Software Engineering Assessment
-- ============================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS sunrise_dental_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_db;

-- 2. Drop existing tables in reverse dependency order (safe reset)
DROP TABLE IF EXISTS notification_logs;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS dentists;
DROP TABLE IF EXISTS users;

-- ============================================================================
-- Table: users (Staff & Administrators with Password Security)
-- ============================================================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'STAFF',
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- Table: notification_logs (Audit Trail for Patient Alerts)
-- ============================================================================
CREATE TABLE notification_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_phone VARCHAR(50) NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notification_recipient (recipient_phone)
) ENGINE=InnoDB;

-- ============================================================================
-- Table: dentists (Clinic Doctors / Dental Specialists)
-- ============================================================================
CREATE TABLE dentists (
    dentist_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- Table: treatments (Dental Procedures Catalog & Standard Fees)
-- ============================================================================
CREATE TABLE treatments (
    treatment_id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name VARCHAR(100) NOT NULL,
    standard_fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- Table: patients (Registered Patients & Demographics)
-- ============================================================================
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient_contact (contact_number)
) ENGINE=InnoDB;

-- ============================================================================
-- Table: appointments (Patient Booking & Clinical Schedule)
-- ============================================================================
CREATE TABLE appointments (
    appointment_number INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    dentist_id INT NOT NULL,
    treatment_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_appointments_dentist
        FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_appointments_treatment
        FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_appointment_status (status)
) ENGINE=InnoDB;

-- ============================================================================
-- Table: bills (Invoicing, Consultation Fees, and Revenue Records)
-- ============================================================================
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number INT NOT NULL UNIQUE,
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 1500.00,
    treatment_cost DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PAID',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bills_appointment
        FOREIGN KEY (appointment_number) REFERENCES appointments(appointment_number)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_bill_payment_status (payment_status)
) ENGINE=InnoDB;

-- ============================================================================
-- SEED DATA
-- ============================================================================

-- 1. Seed Users (passwords hashed with SHA-256 matching UserDAOImpl)
--    admin / admin123 (must_change_password = FALSE)
--    receptionist / reception123 (must_change_password = FALSE)
--    staff / staff123 (must_change_password = TRUE for testing first login reset)
INSERT INTO users (username, password_hash, full_name, role, must_change_password) VALUES
('admin', SHA2('admin123', 256), 'System Administrator', 'ADMIN', FALSE),
('receptionist', SHA2('reception123', 256), 'Senior Receptionist', 'RECEPTIONIST', FALSE),
('staff', SHA2('staff123', 256), 'Clinic Staff Member', 'STAFF', TRUE);

-- 2. Seed Dentists
INSERT INTO dentists (name, specialization, contact_number) VALUES
('Dr. Kasun Perera', 'Orthodontics & Cosmetic Dentistry', '0771234567'),
('Dr. Anoma Silva', 'Endodontics & Oral Surgery', '0719876543'),
('Dr. Rohan Jayasinghe', 'General Dentistry & Periodontics', '0765554321');

-- 3. Seed Treatments Catalog
INSERT INTO treatments (treatment_name, standard_fee) VALUES
('Teeth Cleaning & Scaling', 2500.00),
('Root Canal Therapy', 8500.00),
('Tooth Extraction', 3500.00),
('Composite Dental Filling', 4000.00),
('Teeth Whitening & Bleaching', 12000.00),
('Dental Crown & Bridge Fitting', 18000.00);

-- 4. Seed Patients
INSERT INTO patients (full_name, address, contact_number) VALUES
('Nimal Fernando', 'No. 45 Galle Road, Colombo 03', '0773344556'),
('Sunethra Bandara', 'No. 12 Kandy Road, Kiribathgoda', '0712233445'),
('Chaminda Vass', 'No. 78 Marine Drive, Bambalapitiya', '0789988776');

-- 5. Seed Initial Appointments
INSERT INTO appointments (patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status) VALUES
(1, 1, 1, '2026-09-02', '09:30:00', 'SCHEDULED'),
(2, 2, 2, '2026-09-02', '11:00:00', 'SCHEDULED'),
(3, 3, 3, '2026-09-01', '14:30:00', 'COMPLETED');

-- 6. Seed Initial Bills
INSERT INTO bills (appointment_number, consultation_fee, treatment_cost, total_amount, payment_status, issued_at) VALUES
(3, 1500.00, 3500.00, 5000.00, 'PAID', '2026-09-01 15:00:00');
