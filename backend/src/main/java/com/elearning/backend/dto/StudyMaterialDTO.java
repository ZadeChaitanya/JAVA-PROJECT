package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StudyMaterialDTO {

    private Long id;
    private String title;
    private String fileType; // e.g., "pdf", "notes"
    private LocalDateTime uploadDate;

    // New fields for filtering/access control
    private Integer targetStandard;
    private String subject;

    // Flattened Teacher data
    private Long teacherId;
    private String teacherName;
}