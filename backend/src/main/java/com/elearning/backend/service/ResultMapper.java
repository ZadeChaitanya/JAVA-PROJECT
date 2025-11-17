package com.elearning.backend.service; // Or a dedicated 'mapper' package

import com.elearning.backend.dto.TestResultDTO;
import com.elearning.backend.model.StudentTestResult;
import org.springframework.stereotype.Service;

@Service
public class ResultMapper {

    public TestResultDTO toDto(StudentTestResult entity) {
        TestResultDTO dto = new TestResultDTO();

        // 1. Map properties from the main entity
        dto.setId(entity.getId());
        dto.setScore(entity.getScore());
        dto.setTotalQuestions(entity.getTotalQuestions());

        // 2. Map properties from the nested Student entity
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getName());
        }

        // 3. Map properties from the nested Test entity
        if (entity.getTest() != null) {
            dto.setTestId(entity.getTest().getId());
            dto.setTestTitle(entity.getTest().getTitle());
        }

        return dto;
    }
}