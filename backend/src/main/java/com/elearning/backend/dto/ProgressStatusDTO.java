package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProgressStatusDTO {

    // Content Identification
    private Long contentId;
    private String contentType; // e.g., LECTURE, MATERIAL
    private String title;       // The Title of the VideoLecture or StudyMaterial
    private String subject;     // The Subject of the content

    // Progress Status
    private boolean isCompleted;
    private LocalDateTime completionDate;

    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }
}