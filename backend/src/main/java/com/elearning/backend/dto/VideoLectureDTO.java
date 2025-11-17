package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class VideoLectureDTO {

    private Long id;
    private String title;
    // Note: We deliberately exclude filePath for security, but include it if needed for the frontend.
    private String duration;
    private LocalDateTime uploadDate;

    // Flattened Teacher data
    private Long teacherId;
    private String teacherName;
}