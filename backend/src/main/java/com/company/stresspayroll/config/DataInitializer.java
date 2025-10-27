package com.company.stresspayroll.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.company.stresspayroll.model.Attendance;
import com.company.stresspayroll.model.EmployeeProfile;
import com.company.stresspayroll.model.Reminder;
import com.company.stresspayroll.model.User;
import com.company.stresspayroll.repository.AttendanceRepository;
import com.company.stresspayroll.repository.EmployeeProfileRepository;
import com.company.stresspayroll.repository.ReminderRepository;
import com.company.stresspayroll.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create HR Admin user
        User hrAdmin = new User();
        hrAdmin.setUsername("hr_admin");
        hrAdmin.setEmail("hr@company.com");
        hrAdmin.setPasswordHash(passwordEncoder.encode("password123"));
        hrAdmin.setFullName("HR Administrator");
        hrAdmin.setRole(User.Role.HR);
        hrAdmin = userRepository.save(hrAdmin);

        // Create Employee user
        User employee = new User();
        employee.setUsername("john_doe");
        employee.setEmail("john.doe@company.com");
        employee.setPasswordHash(passwordEncoder.encode("password123"));
        employee.setFullName("John Doe");
        employee.setRole(User.Role.EMPLOYEE);
        employee = userRepository.save(employee);

        // Create Employee Profile
        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(employee);
        profile.setPhone("+1234567890");
        profile.setDepartment("Engineering");
        profile.setPosition("Software Developer");
        profile.setBaseSalary(new BigDecimal("75000.00"));
        profile.setPaidLeavesPerMonth(2);
        employeeProfileRepository.save(profile);

        // Create sample attendance records
        Attendance attendance1 = new Attendance(employee, LocalDate.now().minusDays(5), Attendance.AttendanceStatus.PRESENT);
        Attendance attendance2 = new Attendance(employee, LocalDate.now().minusDays(4), Attendance.AttendanceStatus.PRESENT);
        Attendance attendance3 = new Attendance(employee, LocalDate.now().minusDays(3), Attendance.AttendanceStatus.PAID_LEAVE);
        Attendance attendance4 = new Attendance(employee, LocalDate.now().minusDays(2), Attendance.AttendanceStatus.PRESENT);
        Attendance attendance5 = new Attendance(employee, LocalDate.now().minusDays(1), Attendance.AttendanceStatus.PRESENT);
        
        attendanceRepository.save(attendance1);
        attendanceRepository.save(attendance2);
        attendanceRepository.save(attendance3);
        attendanceRepository.save(attendance4);
        attendanceRepository.save(attendance5);

        // Create sample reminders
        Reminder reminder1 = new Reminder(employee, "Schedule team meeting for project review", false);
        Reminder reminder2 = new Reminder(employee, "Submit monthly report to manager", true);
        Reminder reminder3 = new Reminder(employee, "Update project documentation", false);
        
        reminderRepository.save(reminder1);
        reminderRepository.save(reminder2);
        reminderRepository.save(reminder3);

        System.out.println("Sample data initialized successfully!");
        System.out.println("HR Admin: hr_admin / password123");
        System.out.println("Employee: john_doe / password123");
    }
}
