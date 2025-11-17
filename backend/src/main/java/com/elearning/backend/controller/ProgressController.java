package com.elearning.backend.controller;

import com.elearning.backend.dto.ProgressStatusDTO;
import com.elearning.backend.model.StudentProgress;
import com.elearning.backend.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    // NEW API: POST /api/progress/complete (Mark Item as Complete)
    // Secured by JWT token
    @PostMapping("/complete")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<StudentProgress> markComplete(
            Principal principal,
            @RequestParam Long contentId,
            @RequestParam String contentType
    ) {
        // Get the authenticated user's email
        String studentEmail = principal.getName();

        // Delegate to service to mark complete or update existing record
        StudentProgress progress = progressService.markContentComplete(
                studentEmail, contentId, contentType
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(progress);
    }
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')") // Only students should check their status
    public ResponseEntity<List<ProgressStatusDTO>> getProgressStatus(Principal principal) {

        // Delegate to service, passing the authenticated user's email
        List<ProgressStatusDTO> statusList = progressService.getProgressStatus(principal.getName());

        return ResponseEntity.ok(statusList);
    }

    // NOTE: The GET /api/progress/status endpoint will be implemented later.
}