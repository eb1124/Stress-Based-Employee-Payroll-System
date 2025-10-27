package com.company.stresspayroll.model;


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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "stress_records")
public class StressRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @Positive
    @Column(name = "record_month")
    private Integer month;
    
    @NotNull
    @Positive
    @Column(name = "record_year")
    private Integer year;
    
    @Column(name = "overtime_hours")
    private Integer overtimeHours = 0;
    
    @Column(name = "overtime_reason", columnDefinition = "TEXT")
    private String overtimeReason;
    
    @Min(1)
    @Max(10)
    @Column(name = "stress_level")
    private Integer stressLevel;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public StressRecord() {}
    
    public StressRecord(User user, Integer month, Integer year, Integer overtimeHours,                       String overtimeReason, Integer stressLevel) {
        this.user = user;
        this.month = month;
        this.year = year;
        this.overtimeHours = overtimeHours;
        this.overtimeReason = overtimeReason;
        this.stressLevel = stressLevel;
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
    
    public Integer getOvertimeHours() {
        return overtimeHours;
    }
    
    public void setOvertimeHours(Integer overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
    
    public String getOvertimeReason() {
        return overtimeReason;
    }
    
    public void setOvertimeReason(String overtimeReason) {
        this.overtimeReason = overtimeReason;
    }
    
    public Integer getStressLevel() {
        return stressLevel;
    }
    
    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
