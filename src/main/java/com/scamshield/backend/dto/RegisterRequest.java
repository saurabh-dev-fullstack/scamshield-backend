package com.scamshield.backend.dto;

public class RegisterRequest {

    private String email;
    private String password;
    private String role; // ADMIN or USER

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
