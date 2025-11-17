package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class TestResultDetailDTO {
    private Long resultId;

    // Result Summary
    private int score;
    private int totalQuestions;
    private LocalDateTime dateTaken;

    // Test Information
    private Long testId;
    private String testTitle;

    // Feedback List (The nested structure)
    private List<QuestionFeedbackDTO> feedback;
}