package com.company.stresspayroll.repository;

import com.company.stresspayroll.model.Reminder;
import com.company.stresspayroll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    
    List<Reminder> findByUserOrderByCreatedAtDesc(User user);
    
    List<Reminder> findByUserAndIsCompletedOrderByCreatedAtDesc(User user, Boolean isCompleted);
    
    Optional<Reminder> findByIdAndUser(Long id, User user);
}
