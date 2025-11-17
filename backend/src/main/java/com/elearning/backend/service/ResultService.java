package com.elearning.backend.service;

import com.elearning.backend.dto.QuestionFeedbackDTO;
import com.elearning.backend.dto.TestResultDetailDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentTestResult;
import com.elearning.backend.model.Question;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.StudentTestResultRepository;
import com.elearning.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final StudentTestResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final StudentRepository studentRepository;
    // NOTE: You will need a way to store and retrieve the student's actual answers.
    // Assuming for now it's done via custom logic in the result entity or another repository.

    /**
     * Retrieves a detailed test result, including question feedback and correctness status.
     * @param resultId The ID of the student's completed test result record.
     * @return A DTO containing the result summary and a list of question-by-question feedback.
     */
    public TestResultDetailDTO getDetailedResult(Long resultId, String loggedInUserEmail) throws AccessDeniedException {

        Student loggedInStudent = studentRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in student profile not found."));

        Long loggedInUserId = loggedInStudent.getId(); // Get the secure Long ID

        // 2. Fetch the Test Result
        StudentTestResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Test Result not found..."));

        // 3. FINAL SECURE CHECK: Compare database ID of result owner vs. token owner's ID
        if (!result.getStudent().getId().equals(loggedInUserId)) {
            // This is the final 403 gate. If this is hit, the access is correctly denied.
            throw new AccessDeniedException("Access denied. You do not own this test result.");
        }

        // 2. Fetch all questions related to this test.
        Long testId = result.getTest().getId();
        // Assuming your QuestionRepository has a working findByTestId method:
        List<Question> testQuestions = questionRepository.findByTestId(testId);

        // 3. Map Questions to Feedback DTOs (The Comparison Logic)
        List<QuestionFeedbackDTO> feedbackList = testQuestions.stream()
                .map(question -> createFeedbackDTO(question, result))
                .collect(Collectors.toList());

        // 4. Assemble the Final Detailed DTO
        TestResultDetailDTO dto = new TestResultDetailDTO();
        dto.setResultId(result.getId());
        dto.setScore(result.getScore());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setDateTaken(result.getDateTaken());
        dto.setTestId(testId);
        dto.setTestTitle(result.getTest().getTitle());
        dto.setFeedback(feedbackList);

        return dto;
    }

    /**
     * Helper method to map Question entity and result data to the feedback DTO.
     * NOTE: THIS METHOD CONTAINS PLACEHOLDER LOGIC FOR STUDENT ANSWERS.
     * You will need to implement the actual comparison based on how student answers are stored.
     */
    private QuestionFeedbackDTO createFeedbackDTO(Question question, StudentTestResult result) {
        QuestionFeedbackDTO feedback = new QuestionFeedbackDTO();
        String studentSubmittedAnswer = result.getStudentAnswers().stream()
                .filter(answer -> answer.getQuestion().getId().equals(question.getId()))
                .findFirst()
                .map(answer -> answer.getSubmittedAnswer())
                .orElse(null); // Set to null if the student skipped the question

        // Map Question Details
        feedback.setQuestionId(question.getId());
        feedback.setQuestionIndex(question.getQuestionIndex());
        feedback.setQuestionText(question.getQuestionText());
        feedback.setOptionA(question.getOptionA());
        feedback.setOptionB(question.getOptionB());
        feedback.setOptionC(question.getOptionC());
        feedback.setOptionD(question.getOptionD());



        feedback.setCorrectAnswer(question.getCorrectAnswer());
        feedback.setStudentAnswer(studentSubmittedAnswer);

        // Comparison logic
        feedback.setIsCorrect(studentSubmittedAnswer != null &&
                studentSubmittedAnswer.equals(question.getCorrectAnswer()));

        return feedback;
    }
}