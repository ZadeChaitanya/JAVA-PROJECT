package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_progress")
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The student who owns this progress record (FK: student_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // The content being tracked (ID of VideoLecture or StudyMaterial)
    private Long contentId;

    // Type of content (LECTURE, MATERIAL)
    private String contentType;

    private boolean isCompleted = false;

    private LocalDateTime completionDate;

    // Optional: Add a unique constraint to prevent duplicate "complete" entries
    // @Column(unique = true) is often not suitable for composite keys in JPA without further config.
}