package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Create this DTO in the com.elearning.backend.dto package
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String email;    // Matches the username field
    private String password; // Matches the raw password field
}