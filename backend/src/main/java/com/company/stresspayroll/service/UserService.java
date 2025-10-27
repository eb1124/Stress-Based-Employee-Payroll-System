package com.company.stresspayroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.stresspayroll.model.EmployeeProfile;
import com.company.stresspayroll.model.User;
import com.company.stresspayroll.repository.EmployeeProfileRepository;
import com.company.stresspayroll.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }
    
    public User registerUser(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, email, passwordEncoder.encode(password), fullName, User.Role.EMPLOYEE);
        return userRepository.save(user);
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public EmployeeProfile getEmployeeProfile(User user) {
        return employeeProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee profile not found for user: " + user.getUsername()));
    }
    
    public EmployeeProfile createEmployeeProfile(User user, String phone, String department,
                                    String position, java.math.BigDecimal baseSalary) {
        EmployeeProfile profile = new EmployeeProfile(user, phone, department, position, baseSalary, 2);
        return employeeProfileRepository.save(profile);
    }
    
    public EmployeeProfile updateEmployeeProfile(User user, String phone, String department, String position) {
        EmployeeProfile profile = getEmployeeProfile(user);
        profile.setPhone(phone);
        profile.setDepartment(department);
        profile.setPosition(position);
        return employeeProfileRepository.save(profile);
    }
}
