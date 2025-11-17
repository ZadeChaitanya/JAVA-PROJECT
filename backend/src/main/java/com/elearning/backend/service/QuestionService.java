package com.elearning.backend.service;

import com.elearning.backend.model.Question;
import com.elearning.backend.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {

        // 1. Get the ID of the Test this question belongs to
        Long testId = question.getTest().getId();

        // 2. Find the highest existing index for that specific test
        Integer maxIndex = questionRepository.findMaxQuestionIndexByTestId(testId);

        // 3. Calculate the next index: If null (first question), set to 1; otherwise, max + 1.
        int nextIndex = (maxIndex == null) ? 1 : maxIndex + 1;

        // 4. Set the calculated index on the new question object
        question.setQuestionIndex(nextIndex);

        // 5. Save the question to the database
        return questionRepository.save(question);
    }
}