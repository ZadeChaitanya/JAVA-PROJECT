package com.elearning.backend.service;

import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Question;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.model.Test;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TeacherRepository teacherRepository;
    private final QuestionService questionService; // For index calculation

    public Test createTestWithQuestions(Test test, String teacherEmail) {

        // 1. AUTHORIZATION: Find the Teacher entity using the secured email from the token
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with email: " + teacherEmail));

        // 2. Link the Teacher entity to the Test
        test.setTeacher(teacher);

        // 3. Save the Test parent entity (if ID is null, this performs INSERT)
        Test savedTest = testRepository.save(test);

        // 4. Handle nested Questions
        if (test.getQuestions() != null) {
            for (Question question : test.getQuestions()) {

                // CRUCIAL: Set the parent reference on the child object
                question.setTest(savedTest);

                // 5. Use the QuestionService to calculate the index and save the question
                // This ensures questionIndex (1, 2, 3...) is correctly set.
                questionService.saveQuestion(question);
            }
        }

        // Return the fully persisted Test object with children
        return savedTest;
    }
}