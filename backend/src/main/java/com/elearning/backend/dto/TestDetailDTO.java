package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class TestDetailDTO {
    private Long id;
    private String title;
    private LocalDate date;

    // Teacher Info (Flattened/Cleaned)
    private Long teacherId;
    private String teacherName;
    private String teacherSubject;

    // Nested Questions
    private List<QuestionDTO> questions;
    // Assuming you have a QuestionDTO that already filters question details if needed
}