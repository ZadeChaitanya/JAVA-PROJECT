package com.elearning.backend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// NOTE: This controller is automatically protected by SecurityConfig.java

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // This endpoint demonstrates extracting user details from the security context
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {

        // 1. Get the authenticated user object from Spring's SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            String email = userDetails.getUsername();

            // 2. Return a simple confirmation message
            return ResponseEntity.ok("Profile Access Granted. User: " + email + ", Role: " + role);

        } else {
            // This case should theoretically not be hit if the JwtAuthFilter works correctly
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Please log in.");
        }
    }
}