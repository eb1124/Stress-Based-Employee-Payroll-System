package com.company.stresspayroll.repository;

import com.company.stresspayroll.model.StressRecord;
import com.company.stresspayroll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StressRecordRepository extends JpaRepository<StressRecord, Long> {
    
    List<StressRecord> findByUserOrderByYearDescMonthDesc(User user);
    
    Optional<StressRecord> findByUserAndMonthAndYear(User user, Integer month, Integer year);
    
    @Query("SELECT sr FROM StressRecord sr WHERE sr.user = :user ORDER BY sr.year DESC, sr.month DESC")
    List<StressRecord> findLatestStressRecordsByUser(@Param("user") User user);
    
    List<StressRecord> findByStressLevelGreaterThanOrderByCreatedAtDesc(int stressLevel);
}
