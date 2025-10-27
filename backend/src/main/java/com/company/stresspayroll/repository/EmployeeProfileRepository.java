package com.company.stresspayroll.repository;

import com.company.stresspayroll.model.EmployeeProfile;
import com.company.stresspayroll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    
    Optional<EmployeeProfile> findByUser(User user);
    
    Optional<EmployeeProfile> findByUserId(Long userId);
}
