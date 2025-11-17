package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.Data; // Using Lombok for brevity
import java.time.LocalDateTime;

@Entity
@Data // Generates getters, setters, constructors, etc.
@Table(name = "video_lectures")
public class VideoLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int targetStandard;
    private String subject;

    // CRITICAL: The path where the video file is stored on the server
    private String filePath;

    private String duration; // e.g., "45:30"
    private LocalDateTime uploadDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // Optional: Link to a specific course or subject
    // @ManyToOne
    // private Course course;
}