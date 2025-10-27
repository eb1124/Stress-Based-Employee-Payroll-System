package com.company.stresspayroll.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AuthRequest {
    
    @NotBlank
    private String username;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    // Constructors
    public AuthRequest() {}
    
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
