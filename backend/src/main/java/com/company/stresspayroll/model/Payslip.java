package com.company.stresspayroll.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "payslips")
public class Payslip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @Positive
    @Column(name = "payslip_month")
    private Integer month;
    
    @NotNull
    @Positive
    @Column(name = "payslip_year")
    private Integer year;
    
    @NotNull
    @Column(name = "base_salary")
    private BigDecimal baseSalary;
    
    @Column(name = "unpaid_leave_deductions")
    private BigDecimal unpaidLeaveDeductions = BigDecimal.ZERO;
    
    @NotNull
    @Column(name = "final_salary")
    private BigDecimal finalSalary;
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Payslip() {}
    
    public Payslip(User user, Integer month, Integer year, BigDecimal baseSalary,                   BigDecimal unpaidLeaveDeductions, BigDecimal finalSalary) {
        this.user = user;
        this.month = month;
        this.year = year;
        this.baseSalary = baseSalary;
        this.unpaidLeaveDeductions = unpaidLeaveDeductions;
        this.finalSalary = finalSalary;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
