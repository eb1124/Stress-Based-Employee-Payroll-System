package com.company.stresspayroll.repository;

import com.company.stresspayroll.model.Payslip;
import com.company.stresspayroll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    
    List<Payslip> findByUserOrderByYearDescMonthDesc(User user);
    
    Optional<Payslip> findByUserAndMonthAndYear(User user, Integer month, Integer year);
    
    @Query("SELECT COUNT(p) FROM Payslip p WHERE p.user = :user AND p.month = :month AND p.year = :year")
    long countByUserAndMonthAndYear(@Param("user") User user, @Param("month") Integer month, @Param("year") Integer year);
    
    List<Payslip> findTop10ByOrderByGeneratedAtDesc();
}
