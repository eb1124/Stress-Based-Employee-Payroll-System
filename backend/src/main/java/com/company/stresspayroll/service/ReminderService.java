package com.company.stresspayroll.service;

import com.company.stresspayroll.model.Reminder;
import com.company.stresspayroll.model.User;
import com.company.stresspayroll.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    public List<Reminder> getRemindersByUser(User user) {
        return reminderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Reminder createReminder(User user, String reminderText) {
        Reminder reminder = new Reminder(user, reminderText, false);
        return reminderRepository.save(reminder);
    }
    
    public Reminder updateReminder(Long reminderId, String reminderText, Boolean isCompleted) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        
        reminder.setReminderText(reminderText);
        reminder.setIsCompleted(isCompleted);
        
        return reminderRepository.save(reminder);
    }
    
    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
