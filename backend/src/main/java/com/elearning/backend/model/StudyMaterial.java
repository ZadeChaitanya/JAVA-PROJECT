package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String fileType; // pdf, docx, video
    private String filePath;
    private LocalDateTime uploadDate;
    private int targetStandard;
    private String subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public StudyMaterial(String title, String fileType, String string, LocalDateTime now, Teacher teacher) {
    }
}
