# Stress-Based Employee Payroll System

A full-stack web application for managing employee payroll, wellness, and stress levels with separate views for employees and HR administrators.

## Technology Stack

- **Backend**: Java Spring Boot
- **Frontend**: HTML, CSS, Vanilla JavaScript
- **Database**: MySQL/PostgreSQL
- **Authentication**: JWT with Spring Security

## Features

### Employee Portal
- Profile management
- Payslip generation with stress assessment
- Wellness tips and reminders
- Stress level tracking and dashboard
- Attendance management

### HR Portal
- Employee management
- Payroll oversight
- Stress monitoring
- Attendance tracking

## Project Structure

```
stress-payroll-system/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/               # HTML, CSS, JavaScript
│   ├── css/
│   ├── js/
│   └── *.html
├── database/              # SQL scripts
└── README.md
```

## Getting Started

1. Set up MySQL/PostgreSQL database
2. Run SQL scripts in database/ folder
3. Configure database connection in backend
4. Start Spring Boot application
5. Open frontend in web browser

## API Endpoints

### Authentication
- POST /auth/register
- POST /auth/login

### Employee APIs
- GET /api/employee/profile
- PUT /api/employee/profile
- GET /api/employee/payslips
- POST /api/employee/payslips/generate
- POST /api/employee/stress-record
- GET /api/employee/stress-dashboard
- GET /api/employee/wellness-tips
- GET /api/employee/reminders
- POST /api/employee/reminders

### HR APIs
- GET /api/hr/employees
- GET /api/hr/employee/{userId}/profile
- GET /api/hr/employee/{userId}/payslips
- GET /api/hr/employee/{userId}/stress-history
- GET /api/hr/employee/{userId}/attendance
