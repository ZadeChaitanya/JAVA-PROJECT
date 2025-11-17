package com.elearning.backend.service;

import com.elearning.backend.dto.QuestionDTO; // Assuming this DTO exists
import com.elearning.backend.dto.TestDetailDTO;
import com.elearning.backend.model.Test;
import com.elearning.backend.model.Teacher; // Required for entity check
import com.elearning.backend.model.Question; // Required for nested mapping
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestMapper {

    /**
     * Converts a fully loaded Test entity (with nested Questions) to a secure DTO.
     */
    public TestDetailDTO toDto(Test entity) {
        TestDetailDTO dto = new TestDetailDTO();

        // 1. Map core Test fields
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDate(entity.getDate());

        // 2. Map Teacher details securely (Flattens the teacher object)
        Teacher teacher = entity.getTeacher();
        if (teacher != null) {
            dto.setTeacherId(teacher.getId());
            dto.setTeacherName(teacher.getName());
            dto.setTeacherSubject(teacher.getSubject());
        }

        // 3. Map nested Questions to QuestionDTOs
        if (entity.getQuestions() != null) {
            // NOTE: This assumes you have a functional way to map Question -> QuestionDTO
            // For example, by having a constructor in QuestionDTO that takes a Question entity.

            List<QuestionDTO> questionDtos = entity.getQuestions().stream()
                    .map(QuestionDTO::new) // Assuming QuestionDTO has a constructor accepting a Question entity
                    .collect(Collectors.toList());

            dto.setQuestions(questionDtos);
        }

        return dto;
    }
}