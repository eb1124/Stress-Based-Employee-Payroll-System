# Stress Payroll System Setup Guide

## Prerequisites

1. **Java 17** - Download and install from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [OpenJDK](https://adoptium.net/)
2. **MySQL 8.0+** - Download and install from [MySQL](https://dev.mysql.com/downloads/mysql/)
3. **Maven 3.6+** - Download from [Apache Maven](https://maven.apache.org/download.cgi)

## Database Setup

### Option 1: Using MySQL Command Line
```bash
# Start MySQL service
# On Windows: Start MySQL service from Services
# On Linux/Mac: sudo systemctl start mysql

# Connect to MySQL as root
mysql -u root -p

# Create database
CREATE DATABASE stress_payroll_system;
USE stress_payroll_system;

# Run the schema file
SOURCE database/schema.sql;

# Verify tables were created
SHOW TABLES;

# Exit MySQL
EXIT;
```

### Option 2: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Create a new schema named `stress_payroll_system`
4. Open the `database/schema.sql` file
5. Execute the SQL script

## Configuration

### Database Connection
Update `backend/src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stress_payroll_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

## Running the Application

### Backend (Spring Boot)
```bash
# Navigate to backend directory
cd backend

# Run the application
mvn spring-boot:run

# Or build and run
mvn clean package
java -jar target/stress-payroll-system-0.0.1-SNAPSHOT.jar
```

The backend will start on `http://localhost:9091`

### Frontend
1. Open `frontend/index.html` in your web browser
2. Or serve it using a local web server:
   ```bash
   # Using Python (if installed)
   cd frontend
   python -m http.server 3000
   
   # Using Node.js (if installed)
   npx http-server -p 3000
   ```

## Default Login Credentials

The system comes with sample users:

### HR Admin
- **Username:** `hr_admin`
- **Password:** `password123`
- **Role:** HR

### Employee
- **Username:** `john_doe`
- **Password:** `password123`
- **Role:** Employee

## Features

### Employee Portal
- View and edit profile
- Generate and view payslips
- Track stress levels
- View wellness tips
- Manage reminders

### HR Portal
- View all employees
- Monitor employee stress levels
- Manage payroll
- Track attendance
- View employee details

## API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration

### Employee APIs
- `GET /api/employee/profile` - Get employee profile
- `PUT /api/employee/profile` - Update employee profile
- `GET /api/employee/payslips` - Get employee payslips
- `POST /api/employee/payslips/generate` - Generate payslip
- `POST /api/employee/stress-record` - Add stress record
- `GET /api/employee/stress-dashboard` - Get stress dashboard
- `GET /api/employee/wellness-tips` - Get wellness tips
- `GET /api/employee/reminders` - Get reminders
- `POST /api/employee/reminders` - Add reminder
- `PUT /api/employee/reminders/{id}` - Update reminder

### HR APIs
- `GET /api/hr/employees` - Get all employees
- `GET /api/hr/employee/{userId}/profile` - Get employee profile
- `GET /api/hr/employee/{userId}/payslips` - Get employee payslips
- `GET /api/hr/employee/{userId}/stress-history` - Get employee stress history
- `GET /api/hr/employee/{userId}/attendance` - Get employee attendance
- `POST /api/hr/employee/{userId}/attendance` - Add attendance record
- `GET /api/hr/dashboard` - Get HR dashboard

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in `application.properties`
   - Ensure database `stress_payroll_system` exists

2. **Port Already in Use**
   - Change port in `application.properties`: `server.port=8081`
   - Or kill the process using port 8080

3. **CORS Issues**
   - The application is configured to allow CORS from `http://localhost:3000`
   - If serving frontend from different port, update CORS configuration

4. **Authentication Issues**
   - Ensure JWT secret is configured
   - Check if user exists in database
   - Verify password is correctly hashed

### Logs
Check the console output for detailed error messages. The application logs at DEBUG level for troubleshooting.

## Development

### Project Structure
```
stress-payroll-system/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml            # Maven dependencies
├── frontend/               # HTML, CSS, JavaScript
│   ├── css/               # Stylesheets
│   ├── js/                # JavaScript files
│   └── *.html             # HTML pages
├── database/              # SQL scripts
└── README.md             # This file
```

### Adding New Features
1. Add new entities in `backend/src/main/java/com/company/stresspayroll/model/`
2. Create repositories in `backend/src/main/java/com/company/stresspayroll/repository/`
3. Add services in `backend/src/main/java/com/company/stresspayroll/service/`
4. Create controllers in `backend/src/main/java/com/company/stresspayroll/controller/`
5. Update frontend JavaScript files for new functionality

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the console logs for error messages
3. Verify database setup and configuration
4. Ensure all prerequisites are installed correctly
