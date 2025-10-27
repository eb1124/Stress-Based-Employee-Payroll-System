package com.company.stresspayroll.repository;

import com.company.stresspayroll.model.Attendance;
import com.company.stresspayroll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByUserOrderByDateDesc(User user);
    
    List<Attendance> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user = :user AND a.date BETWEEN :startDate AND :endDate AND a.status = 'UNPAID_LEAVE'")
    long countUnpaidLeavesByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user = :user AND a.date BETWEEN :startDate AND :endDate AND a.status IN ('PAID_LEAVE', 'UNPAID_LEAVE')")
    long countTotalLeavesByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.user = :user AND EXTRACT(MONTH FROM a.date) = :month AND EXTRACT(YEAR FROM a.date) = :year")
    List<Attendance> findByUserAndMonthAndYear(@Param("user") User user, @Param("month") Integer month, @Param("year") Integer year);
    
    List<Attendance> findByUserAndDateAfterOrderByDateDesc(User user, LocalDate date);
    
    boolean existsByUserAndDate(User user, LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE EXTRACT(MONTH FROM a.date) = :month AND EXTRACT(YEAR FROM a.date) = :year")
    long countByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.status = :status AND EXTRACT(MONTH FROM a.date) = :month AND EXTRACT(YEAR FROM a.date) = :year")
    long countByStatusAndMonthAndYear(@Param("status") Attendance.AttendanceStatus status, @Param("month") Integer month, @Param("year") Integer year);
}
