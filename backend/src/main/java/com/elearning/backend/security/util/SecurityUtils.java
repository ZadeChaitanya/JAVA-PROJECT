package com.elearning.backend.security.util;

import com.elearning.backend.model.Student;
import com.elearning.backend.model.Teacher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// NOTE: You must inject and use this class in your services.

@Component
public class SecurityUtils {

    /**
     * Retrieves the email (username) of the currently authenticated user.
     */
    public String getAuthenticatedUserEmail() {
        // The principal is the UserDetails object (Student or Teacher) set by the JwtAuthFilter
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        throw new IllegalStateException("User not authenticated or principal is not UserDetails.");
    }

    /**
     * Retrieves the ID (primary key) of the currently authenticated user.
     * This relies on custom casting since getId() is not in UserDetails.
     */
    public Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Student student) {
            return student.getId();
        } else if (principal instanceof Teacher teacher) {
            return teacher.getId();
        }
        throw new IllegalStateException("Authenticated user type not recognized or ID not available.");
    }
}