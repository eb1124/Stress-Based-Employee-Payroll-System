package com.company.stresspayroll.dto;

import java.math.BigDecimal;

public class PayslipResponse {
    
    private Long id;
    private Integer month;
    private Integer year;
    private BigDecimal baseSalary;
    private BigDecimal unpaidLeaveDeductions;
    private BigDecimal finalSalary;
    private String generatedAt;
    
    // Constructors
    public PayslipResponse() {}
    
    public PayslipResponse(Long id, Integer month, Integer year, BigDecimal baseSalary, 
                          BigDecimal unpaidLeaveDeductions, BigDecimal finalSalary, String generatedAt) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.baseSalary = baseSalary;
        this.unpaidLeaveDeductions = unpaidLeaveDeductions;
        this.finalSalary = finalSalary;
        this.generatedAt = generatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getMonth() {
        return month;
    }
    
    public void setMonth(Integer month) {
        this.month = month;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public BigDecimal getUnpaidLeaveDeductions() {
        return unpaidLeaveDeductions;
    }
    
    public void setUnpaidLeaveDeductions(BigDecimal unpaidLeaveDeductions) {
        this.unpaidLeaveDeductions = unpaidLeaveDeductions;
    }
    
    public BigDecimal getFinalSalary() {
        return finalSalary;
    }
    
    public void setFinalSalary(BigDecimal finalSalary) {
        this.finalSalary = finalSalary;
    }
    
    public String getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
