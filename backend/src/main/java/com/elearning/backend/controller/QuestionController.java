package com.elearning.backend.controller;

import com.elearning.backend.dto.QuestionDTO;
import com.elearning.backend.model.Question;
import com.elearning.backend.repository.QuestionRepository;
import com.elearning.backend.service.QuestionService; // NEW IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173") // adjust for React later
public class QuestionController {

    // Replace field injection with final field for constructor injection
    private final QuestionRepository questionRepository;
    private final QuestionService questionService; // Inject the new service

    // Constructor Injection (Best Practice)
    public QuestionController(QuestionRepository questionRepository, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByTest(@PathVariable Long testId) {
        List<Question> questions = questionRepository.findByTestId(testId);
        List<QuestionDTO> dtos = questions.stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping // This handles the POST request to create a new question
    public ResponseEntity<Question> addQuestion(@RequestBody Question question) {
        // Delegate the creation and index assignment to the service layer
        Question savedQuestion = questionService.saveQuestion(question);
        return ResponseEntity.status(201).body(savedQuestion);
    }
}