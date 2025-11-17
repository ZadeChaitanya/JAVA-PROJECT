package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestResultResponseDTO {
    private Long resultId;
    private Long studentId;
    private Long testId;
    private String status = "Success";
}