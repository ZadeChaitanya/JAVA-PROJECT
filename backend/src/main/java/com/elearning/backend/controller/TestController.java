package com.elearning.backend.controller;
import com.elearning.backend.dto.TestDetailDTO;
import com.elearning.backend.model.Test;
import com.elearning.backend.repository.TestRepository;
import com.elearning.backend.service.TestMapper;
import com.elearning.backend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "http://localhost:3000") // allow frontend later
public class TestController {
    @Autowired
    private TestRepository testRepository;
    private final TestService testService;
    private final TestMapper testMapper;

    public TestController(TestService testService, TestMapper testMapper) {
        this.testService = testService;
        this.testMapper = testMapper;
    }

    @GetMapping
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @GetMapping("/teacher/{id}")
    public List<Test> getTestsByTeacher(@PathVariable Long id) {
        return testRepository.findByTeacherId(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<TestDetailDTO> createTest(@RequestBody Test test, Principal principal) { // <-- Change return type to DTO

        // 1. Service handles creation, links the authenticated teacher, and saves questions
        Test savedEntity = testService.createTestWithQuestions(test, principal.getName());

        // 2. Map the created Entity to the secure DTO
        TestDetailDTO dto = testMapper.toDto(savedEntity);

        // 3. Return the created DTO (securely hides the password hash)
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}

