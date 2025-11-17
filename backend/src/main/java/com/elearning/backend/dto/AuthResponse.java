package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Create this DTO in the com.elearning.backend.dto package
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role; // e.g., ROLE_STUDENT or ROLE_TEACHER
    private Long id;     // The ID of the logged-in user
}