package com.company.stresspayroll.controller;

import com.company.stresspayroll.dto.PayslipResponse;
import com.company.stresspayroll.model.*;
import com.company.stresspayroll.repository.AttendanceRepository;
import com.company.stresspayroll.repository.PayslipRepository;
import com.company.stresspayroll.repository.StressRecordRepository;
import com.company.stresspayroll.repository.UserRepository;
import com.company.stresspayroll.service.PayrollService;
import com.company.stresspayroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class HRController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PayrollService payrollService;
    
    @Autowired
    private UserRepository userRepository;
    
    
    @Autowired
    private PayslipRepository payslipRepository;
    
    @Autowired
    private StressRecordRepository stressRecordRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
    
    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees(Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<User> employees = userRepository.findByRole(User.Role.EMPLOYEE);
            
            List<Map<String, Object>> response = employees.stream()
                .map(employee -> {
                    try {
                        EmployeeProfile profile = userService.getEmployeeProfile(employee);
                        return Map.<String, Object>of(
                            "id", employee.getId(),
                            "username", employee.getUsername(),
                            "email", employee.getEmail(),
                            "fullName", employee.getFullName(),
                            "phone", profile.getPhone(),
                            "department", profile.getDepartment(),
                            "position", profile.getPosition(),
                            "baseSalary", profile.getBaseSalary(),
                            "createdAt", employee.getCreatedAt()
                        );
                    } catch (Exception e) {
                        return Map.<String, Object>of(
                            "id", employee.getId(),
                            "username", employee.getUsername(),
                            "email", employee.getEmail(),
                            "fullName", employee.getFullName(),
                            "phone", "N/A",
                            "department", "N/A",
                            "position", "N/A",
                            "baseSalary", "N/A",
                            "createdAt", employee.getCreatedAt()
                        );
                    }
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/employee/{userId}/profile")
    public ResponseEntity<?> getEmployeeProfile(@PathVariable Long userId, Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            User employee = userService.getUserById(userId);
            if (employee.getRole() != User.Role.EMPLOYEE) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User is not an employee");
                return ResponseEntity.badRequest().body(error);
            }
            
            EmployeeProfile profile = userService.getEmployeeProfile(employee);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                "id", employee.getId(),
                "username", employee.getUsername(),
                "email", employee.getEmail(),
                "fullName", employee.getFullName(),
                "role", employee.getRole().name(),
                "createdAt", employee.getCreatedAt()
            ));
            response.put("profile", Map.of(
                "phone", profile.getPhone(),
                "department", profile.getDepartment(),
                "position", profile.getPosition(),
                "baseSalary", profile.getBaseSalary(),
                "paidLeavesPerMonth", profile.getPaidLeavesPerMonth(),
                "createdAt", profile.getCreatedAt(),
                "updatedAt", profile.getUpdatedAt()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/employee/{userId}/payslips")
    public ResponseEntity<?> getEmployeePayslips(@PathVariable Long userId, Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            User employee = userService.getUserById(userId);
            if (employee.getRole() != User.Role.EMPLOYEE) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User is not an employee");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Payslip> payslips = payrollService.getPayslipsByUser(employee);
            
            List<PayslipResponse> response = payslips.stream()
                .map(payslip -> new PayslipResponse(
                    payslip.getId(),
                    payslip.getMonth(),
                    payslip.getYear(),
                    payslip.getBaseSalary(),
                    payslip.getUnpaidLeaveDeductions(),
                    payslip.getFinalSalary(),
                    payslip.getGeneratedAt().toString()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/employee/{userId}/stress-history")
    public ResponseEntity<?> getEmployeeStressHistory(@PathVariable Long userId, Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            User employee = userService.getUserById(userId);
            if (employee.getRole() != User.Role.EMPLOYEE) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User is not an employee");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<StressRecord> stressRecords = payrollService.getStressRecordsByUser(employee);
            
            // Calculate statistics
            double averageStress = stressRecords.stream()
                .mapToInt(StressRecord::getStressLevel)
                .average()
                .orElse(0.0);
            
            int maxStress = stressRecords.stream()
                .mapToInt(StressRecord::getStressLevel)
                .max()
                .orElse(0);
            
            int totalOvertimeHours = stressRecords.stream()
                .mapToInt(StressRecord::getOvertimeHours)
                .sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("employee", Map.of(
                "id", employee.getId(),
                "username", employee.getUsername(),
                "fullName", employee.getFullName()
            ));
            response.put("statistics", Map.of(
                "averageStressLevel", Math.round(averageStress * 100.0) / 100.0,
                "maxStressLevel", maxStress,
                "totalOvertimeHours", totalOvertimeHours,
                "totalRecords", stressRecords.size()
            ));
            response.put("stressHistory", stressRecords.stream()
                .map(record -> Map.of(
                    "id", record.getId(),
                    "month", record.getMonth(),
                    "year", record.getYear(),
                    "stressLevel", record.getStressLevel(),
                    "overtimeHours", record.getOvertimeHours(),
                    "overtimeReason", record.getOvertimeReason(),
                    "createdAt", record.getCreatedAt()
                ))
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/employee/{userId}/attendance")
    public ResponseEntity<?> getEmployeeAttendance(@PathVariable Long userId, 
                                                  @RequestParam(required = false) Integer month,
                                                  @RequestParam(required = false) Integer year,
                                                  Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            User employee = userService.getUserById(userId);
            if (employee.getRole() != User.Role.EMPLOYEE) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User is not an employee");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Attendance> attendanceRecords;
            
            if (month != null && year != null) {
                // Get attendance for specific month/year
                attendanceRecords = attendanceRepository.findByUserAndMonthAndYear(employee, month, year);
            } else {
                // Get recent attendance records (last 30 days)
                LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
                attendanceRecords = attendanceRepository.findByUserAndDateAfterOrderByDateDesc(employee, thirtyDaysAgo);
            }
            
            // Calculate statistics
            long presentDays = attendanceRecords.stream()
                .filter(record -> record.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
            
            long paidLeaveDays = attendanceRecords.stream()
                .filter(record -> record.getStatus() == Attendance.AttendanceStatus.PAID_LEAVE)
                .count();
            
            long unpaidLeaveDays = attendanceRecords.stream()
                .filter(record -> record.getStatus() == Attendance.AttendanceStatus.UNPAID_LEAVE)
                .count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("employee", Map.of(
                "id", employee.getId(),
                "username", employee.getUsername(),
                "fullName", employee.getFullName()
            ));
            response.put("statistics", Map.of(
                "presentDays", presentDays,
                "paidLeaveDays", paidLeaveDays,
                "unpaidLeaveDays", unpaidLeaveDays,
                "totalDays", attendanceRecords.size()
            ));
            response.put("attendanceRecords", attendanceRecords.stream()
                .map(record -> Map.of(
                    "id", record.getId(),
                    "date", record.getDate(),
                    "status", record.getStatus().name(),
                    "createdAt", record.getCreatedAt()
                ))
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/employee/{userId}/attendance")
    public ResponseEntity<?> createAttendanceRecord(@PathVariable Long userId, 
                                                   @RequestBody Map<String, Object> request,
                                                   Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            User employee = userService.getUserById(userId);
            if (employee.getRole() != User.Role.EMPLOYEE) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User is not an employee");
                return ResponseEntity.badRequest().body(error);
            }
            
            String dateStr = (String) request.get("date");
            String statusStr = (String) request.get("status");
            
            if (dateStr == null || statusStr == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Date and status are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            LocalDate date = LocalDate.parse(dateStr);
            Attendance.AttendanceStatus status = Attendance.AttendanceStatus.valueOf(statusStr.toUpperCase());
            
            // Check if attendance record already exists for this date
            if (attendanceRepository.existsByUserAndDate(employee, date)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Attendance record already exists for this date");
                return ResponseEntity.badRequest().body(error);
            }
            
            Attendance attendance = new Attendance(employee, date, status);
            attendance = attendanceRepository.save(attendance);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Attendance record created successfully");
            response.put("attendance", Map.of(
                "id", attendance.getId(),
                "date", attendance.getDate(),
                "status", attendance.getStatus().name(),
                "createdAt", attendance.getCreatedAt()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getHRDashboard(Authentication authentication) {
        try {
            User hrUser = getCurrentUser(authentication);
            if (hrUser.getRole() != User.Role.HR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied. HR role required.");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Get total employees
            long totalEmployees = userRepository.countByRole(User.Role.EMPLOYEE);
            
            // Get recent payslips
            List<Payslip> recentPayslips = payslipRepository.findTop10ByOrderByGeneratedAtDesc();
            
            // Get high stress employees (stress level > 7)
            List<StressRecord> highStressRecords = stressRecordRepository.findByStressLevelGreaterThanOrderByCreatedAtDesc(7);
            
            // Get attendance statistics for current month
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            long totalAttendanceRecords = attendanceRepository.countByMonthAndYear(currentMonth, currentYear);
            long presentDays = attendanceRepository.countByStatusAndMonthAndYear(Attendance.AttendanceStatus.PRESENT, currentMonth, currentYear);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statistics", Map.of(
                "totalEmployees", totalEmployees,
                "totalAttendanceRecords", totalAttendanceRecords,
                "presentDays", presentDays,
                "attendanceRate", totalAttendanceRecords > 0 ? Math.round((double) presentDays / totalAttendanceRecords * 100) : 0
            ));
            response.put("recentPayslips", recentPayslips.stream()
                .map(payslip -> Map.of(
                    "id", payslip.getId(),
                    "employeeName", payslip.getUser().getFullName(),
                    "month", payslip.getMonth(),
                    "year", payslip.getYear(),
                    "finalSalary", payslip.getFinalSalary(),
                    "generatedAt", payslip.getGeneratedAt()
                ))
                .collect(Collectors.toList()));
            response.put("highStressEmployees", highStressRecords.stream()
                .map(record -> Map.of(
                    "employeeName", record.getUser().getFullName(),
                    "stressLevel", record.getStressLevel(),
                    "overtimeHours", record.getOvertimeHours(),
                    "month", record.getMonth(),
                    "year", record.getYear()
                ))
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
