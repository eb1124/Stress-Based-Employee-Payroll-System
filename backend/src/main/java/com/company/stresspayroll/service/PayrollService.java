package com.company.stresspayroll.service;

import com.company.stresspayroll.model.*;
import com.company.stresspayroll.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {
    
    @Autowired
    private PayslipRepository payslipRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    @Autowired
    private StressRecordRepository stressRecordRepository;
    
    public Payslip generatePayslip(User user, Integer month, Integer year) {
        // Check if payslip already exists for this month/year
        Optional<Payslip> existingPayslip = payslipRepository.findByUserAndMonthAndYear(user, month, year);
        if (existingPayslip.isPresent()) {
            return existingPayslip.get();
        }
        
        // Get employee profile
        EmployeeProfile profile = employeeProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
        
        // Calculate date range for the month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // Count total leaves taken
        long totalLeaves = attendanceRepository.countTotalLeavesByUserAndDateRange(user, startDate, endDate);
        
        // Calculate unpaid leaves
        int paidLeavesAllowed = profile.getPaidLeavesPerMonth();
        int unpaidLeaves = Math.max(0, (int) totalLeaves - paidLeavesAllowed);
        
        // Calculate daily salary (assuming 22 working days per month)
        BigDecimal dailySalary = profile.getBaseSalary().divide(BigDecimal.valueOf(22), 2, RoundingMode.HALF_UP);
        
        // Calculate deductions
        BigDecimal unpaidLeaveDeductions = dailySalary.multiply(BigDecimal.valueOf(unpaidLeaves));
        
        // Calculate final salary
        BigDecimal finalSalary = profile.getBaseSalary().subtract(unpaidLeaveDeductions);
        
        // Create and save payslip
        Payslip payslip = new Payslip(user, month, year, profile.getBaseSalary(), 
                                    unpaidLeaveDeductions, finalSalary);
        
        return payslipRepository.save(payslip);
    }
    
    public List<Payslip> getPayslipsByUser(User user) {
        return payslipRepository.findByUserOrderByYearDescMonthDesc(user);
    }
    
    public StressRecord createStressRecord(User user, Integer month, Integer year, 
                                         Integer overtimeHours, String overtimeReason) {
        // Calculate stress level based on overtime hours
        int stressLevel = Math.min(10, Math.max(1, overtimeHours / 10 + 1));
        
        // Check if stress record already exists
        Optional<StressRecord> existingRecord = stressRecordRepository.findByUserAndMonthAndYear(user, month, year);
        if (existingRecord.isPresent()) {
            StressRecord record = existingRecord.get();
            record.setOvertimeHours(overtimeHours);
            record.setOvertimeReason(overtimeReason);
            record.setStressLevel(stressLevel);
            return stressRecordRepository.save(record);
        }
        
        StressRecord stressRecord = new StressRecord(user, month, year, overtimeHours, overtimeReason, stressLevel);
        return stressRecordRepository.save(stressRecord);
    }
    
    public List<StressRecord> getStressRecordsByUser(User user) {
        return stressRecordRepository.findByUserOrderByYearDescMonthDesc(user);
    }
    
    public List<StressRecord> getLatestStressRecordsByUser(User user) {
        return stressRecordRepository.findLatestStressRecordsByUser(user);
    }
}
