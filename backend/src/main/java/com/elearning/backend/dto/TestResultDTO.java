package com.elearning.backend.dto;

import lombok.Data;      // Provides Getters, Setters, toString, hashCode, equals
import lombok.NoArgsConstructor; // Provides the default constructor

@Data
@NoArgsConstructor
public class TestResultDTO {

    private Long id;
    private int score;
    private int totalQuestions;

    private Long studentId;
    private String studentName;

    private Long testId;
    private String testTitle;
}