package com.company.stresspayroll.controller;

import com.company.stresspayroll.dto.PayslipResponse;
import com.company.stresspayroll.model.*;
import com.company.stresspayroll.repository.ReminderRepository;
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
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PayrollService payrollService;
    
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            EmployeeProfile profile = userService.getEmployeeProfile(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole().name()
            ));
            response.put("profile", Map.of(
                "phone", profile.getPhone(),
                "department", profile.getDepartment(),
                "position", profile.getPosition(),
                "baseSalary", profile.getBaseSalary(),
                "paidLeavesPerMonth", profile.getPaidLeavesPerMonth()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> profileData, 
                                          Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            String phone = profileData.get("phone");
            String department = profileData.get("department");
            String position = profileData.get("position");
            
            EmployeeProfile updatedProfile = userService.updateEmployeeProfile(user, phone, department, position);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("profile", Map.of(
                "phone", updatedProfile.getPhone(),
                "department", updatedProfile.getDepartment(),
                "position", updatedProfile.getPosition(),
                "baseSalary", updatedProfile.getBaseSalary(),
                "paidLeavesPerMonth", updatedProfile.getPaidLeavesPerMonth()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/payslips")
    public ResponseEntity<?> getPayslips(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<Payslip> payslips = payrollService.getPayslipsByUser(user);
            
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
    
    @PostMapping("/payslips/generate")
    public ResponseEntity<?> generatePayslip(@RequestBody Map<String, Integer> request, 
                                           Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Integer month = request.get("month");
            Integer year = request.get("year");
            
            if (month == null || year == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Month and year are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Payslip payslip = payrollService.generatePayslip(user, month, year);
            PayslipResponse response = new PayslipResponse(
                payslip.getId(),
                payslip.getMonth(),
                payslip.getYear(),
                payslip.getBaseSalary(),
                payslip.getUnpaidLeaveDeductions(),
                payslip.getFinalSalary(),
                payslip.getGeneratedAt().toString()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/stress-record")
    public ResponseEntity<?> createStressRecord(@RequestBody Map<String, Object> request, 
                                              Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Integer month = (Integer) request.get("month");
            Integer year = (Integer) request.get("year");
            Integer overtimeHours = (Integer) request.get("overtimeHours");
            String overtimeReason = (String) request.get("overtimeReason");
            
            if (month == null || year == null || overtimeHours == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Month, year, and overtime hours are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            StressRecord stressRecord = payrollService.createStressRecord(user, month, year, 
                                                                         overtimeHours, overtimeReason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Stress record created successfully");
            response.put("stressRecord", Map.of(
                "id", stressRecord.getId(),
                "month", stressRecord.getMonth(),
                "year", stressRecord.getYear(),
                "overtimeHours", stressRecord.getOvertimeHours(),
                "overtimeReason", stressRecord.getOvertimeReason(),
                "stressLevel", stressRecord.getStressLevel(),
                "createdAt", stressRecord.getCreatedAt()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/stress-dashboard")
    public ResponseEntity<?> getStressDashboard(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<StressRecord> stressRecords = payrollService.getLatestStressRecordsByUser(user);
            
            // Calculate average stress level
            double averageStress = stressRecords.stream()
                .mapToInt(StressRecord::getStressLevel)
                .average()
                .orElse(0.0);
            
            // Get current month's stress level
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            int currentStressLevel = stressRecords.stream()
                .filter(record -> record.getMonth() == currentMonth && record.getYear() == currentYear)
                .mapToInt(StressRecord::getStressLevel)
                .findFirst()
                .orElse(0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("currentStressLevel", currentStressLevel);
            response.put("averageStressLevel", Math.round(averageStress * 100.0) / 100.0);
            response.put("stressHistory", stressRecords.stream()
                .map(record -> Map.of(
                    "month", record.getMonth(),
                    "year", record.getYear(),
                    "stressLevel", record.getStressLevel(),
                    "overtimeHours", record.getOvertimeHours()
                ))
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/wellness-tips")
    public ResponseEntity<?> getWellnessTips() {
        List<Map<String, String>> tips = List.of(
            Map.of("title", "Take Regular Breaks", "description", "Take a 5-10 minute break every hour to reduce eye strain and mental fatigue."),
            Map.of("title", "Practice Deep Breathing", "description", "Spend 2-3 minutes doing deep breathing exercises to reduce stress."),
            Map.of("title", "Stay Hydrated", "description", "Drink at least 8 glasses of water throughout the day to maintain energy levels."),
            Map.of("title", "Get Adequate Sleep", "description", "Aim for 7-9 hours of quality sleep each night for optimal performance."),
            Map.of("title", "Exercise Regularly", "description", "Engage in at least 30 minutes of physical activity most days of the week."),
            Map.of("title", "Maintain Work-Life Balance", "description", "Set clear boundaries between work and personal time to prevent burnout."),
            Map.of("title", "Practice Mindfulness", "description", "Spend 10-15 minutes daily on mindfulness meditation or relaxation techniques."),
            Map.of("title", "Eat Nutritious Meals", "description", "Consume balanced meals with plenty of fruits, vegetables, and whole grains.")
        );
        
        return ResponseEntity.ok(tips);
    }
    
    @GetMapping("/reminders")
    public ResponseEntity<?> getReminders(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<Reminder> reminders = reminderRepository.findByUserOrderByCreatedAtDesc(user);
            
            List<Map<String, Object>> response = reminders.stream()
                .map(reminder -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", reminder.getId());
                    map.put("reminderText", reminder.getReminderText());
                    map.put("isCompleted", reminder.getIsCompleted());
                    map.put("createdAt", reminder.getCreatedAt());
                    map.put("updatedAt", reminder.getUpdatedAt());
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/reminders")
    public ResponseEntity<?> createReminder(@RequestBody Map<String, String> request, 
                                          Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            String reminderText = request.get("reminderText");
            
            if (reminderText == null || reminderText.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reminder text is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Reminder reminder = new Reminder(user, reminderText, false);
            reminder = reminderRepository.save(reminder);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder created successfully");
            response.put("reminder", Map.of(
                "id", reminder.getId(),
                "reminderText", reminder.getReminderText(),
                "isCompleted", reminder.getIsCompleted(),
                "createdAt", reminder.getCreatedAt()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/reminders/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable Long id, 
                                          @RequestBody Map<String, Boolean> request, 
                                          Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Boolean isCompleted = request.get("isCompleted");
            
            Reminder reminder = reminderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
            
            reminder.setIsCompleted(isCompleted != null ? isCompleted : false);
            reminder = reminderRepository.save(reminder);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder updated successfully");
            response.put("reminder", Map.of(
                "id", reminder.getId(),
                "reminderText", reminder.getReminderText(),
                "isCompleted", reminder.getIsCompleted(),
                "updatedAt", reminder.getUpdatedAt()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
