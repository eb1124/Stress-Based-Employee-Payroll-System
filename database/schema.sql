-- Stress-Based Employee Payroll System Database Schema
-- MySQL/PostgreSQL compatible

-- Create database (uncomment if needed)
-- CREATE DATABASE stress_payroll_system;
-- USE stress_payroll_system;

-- Users table for authentication
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('employee', 'hr') NOT NULL DEFAULT 'employee',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Employee profiles with additional information
CREATE TABLE employee_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(50),
    position VARCHAR(50),
    base_salary DECIMAL(10, 2) NOT NULL,
    paid_leaves_per_month INT DEFAULT 2,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Attendance tracking
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('present', 'paid_leave', 'unpaid_leave') NOT NULL DEFAULT 'present',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, date)
);

-- Payslips generated monthly
CREATE TABLE payslips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month INT NOT NULL CHECK (month >= 1 AND month <= 12),
    year INT NOT NULL,
    base_salary DECIMAL(10, 2) NOT NULL,
    unpaid_leave_deductions DECIMAL(10, 2) DEFAULT 0,
    final_salary DECIMAL(10, 2) NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_month_year (user_id, month, year)
);

-- Stress level tracking
CREATE TABLE stress_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month INT NOT NULL CHECK (month >= 1 AND month <= 12),
    year INT NOT NULL,
    overtime_hours INT DEFAULT 0,
    overtime_reason TEXT,
    stress_level INT CHECK (stress_level >= 1 AND stress_level <= 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_month_year_stress (user_id, month, year)
);

-- Reminders for employees
CREATE TABLE reminders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    reminder_text TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert sample HR user
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('hr_admin', 'hr@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'HR Administrator', 'hr');

-- Insert sample employee
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('john_doe', 'john.doe@company.com', '$2b$12$UgefpbN68UzskgDHDCf.Mug6rgQElYHHAsMJQ6Ed9UNXqc1dQsSxq', 'John Doe', 'employee');

-- Insert sample employee profile
INSERT INTO employee_profiles (user_id, phone, department, position, base_salary, paid_leaves_per_month) VALUES 
(2, '+1234567890', 'Engineering', 'Software Developer', 75000.00, 2);

-- Insert sample attendance records
INSERT INTO attendance (user_id, date, status) VALUES
(2, '2024-01-01', 'present'),
(2, '2024-01-02', 'present'),
(2, '2024-01-03', 'paid_leave'),
(2, '2024-01-04', 'present'),
(2, '2024-01-05', 'present');

-- Insert sample wellness tips (this will be handled by the backend, but we can add some static data)
-- The wellness tips will be returned by the API endpoint, not stored in database

-- Insert sample reminders
INSERT INTO reminders (user_id, reminder_text, is_completed) VALUES
(2, 'Schedule team meeting for project review', FALSE),
(2, 'Submit monthly report to manager', TRUE),
(2, 'Update project documentation', FALSE);
