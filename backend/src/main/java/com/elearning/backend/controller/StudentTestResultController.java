package com.elearning.backend.controller;

import com.elearning.backend.dto.TestResultDTO;             // New DTO Import
import com.elearning.backend.dto.TestResultDetailDTO;
import com.elearning.backend.dto.TestResultResponseDTO;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentTestResult;
import com.elearning.backend.repository.StudentTestResultRepository;
import com.elearning.backend.service.ResultMapper;           // New Mapper Import
import com.elearning.backend.service.ResultService;
import com.elearning.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // Required for stream() mapping

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentTestResultController {

    private final StudentTestResultRepository resultRepository;
    private final ResultMapper resultMapper; // <--- Mapper dependency
    private final ResultService resultService;
    private final StudentService studentService;

    // Constructor Injection (Recommended over field @Autowired)
    public StudentTestResultController(
            StudentTestResultRepository resultRepository,
            ResultMapper resultMapper, ResultService resultService, StudentService studentService) {
        this.resultRepository = resultRepository;
        this.resultMapper = resultMapper;
        this.resultService = resultService;
        this.studentService = studentService;
    }
    @GetMapping
    public List<TestResultDTO> getAllResults() {

        // 1. Fetch all entities from the database
        List<StudentTestResult> entities = resultRepository.findAll();

        // 2. Map List<Entity> to List<DTO> using the mapper service
        return entities.stream()
                .map(resultMapper::toDto)
                .collect(Collectors.toList());
    }

    // GET: /api/results/student/{studentID} - Returns a list of DTOs
    @GetMapping("/student/{studentID}")
    public List<TestResultDTO> getByStudent(@PathVariable("studentID") Long studentID) {

        List<StudentTestResult> entities = resultRepository.findByStudentId(studentID);

        // Map List<Entity> to List<DTO>
        return entities.stream()
                .map(resultMapper::toDto)
                .collect(Collectors.toList());
    }

    // GET: /api/results/test/{testId} - Returns a list of DTOs
    @GetMapping("/test/{testId}")
    public ResponseEntity<List<TestResultDTO>> getResultsByTest(@PathVariable Long testId) {

        List<StudentTestResult> entities = resultRepository.findByTestId(testId);

        // Map List<Entity> to List<DTO>
        List<TestResultDTO> dtos = entities.stream()
                .map(resultMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/{resultId}/details")
    public ResponseEntity<TestResultDetailDTO> getResultDetails(
            Principal principal, // FIX 1: Use Principal (String username/email)
            // REMOVED: @AuthenticationPrincipal Long loggedInUserId
            @PathVariable Long resultId) throws AccessDeniedException {
        // Pass the secure email (username) to the service
        String loggedInUserEmail = principal.getName();

        // FIX 2: Call service with email instead of ID
        TestResultDetailDTO details = resultService.getDetailedResult(resultId,  loggedInUserEmail);

        return ResponseEntity.ok(details);
    }


    // POST: /api/results - The POST method still accepts the full Entity/DTO and saves it.
    // Inside StudentTestResultController.addResult
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping
    public ResponseEntity<TestResultResponseDTO> addResult(Principal principal, @RequestBody StudentTestResult result) {

        // ... logic to get student and set back-references (omitted for brevity) ...
        String loggedInUserEmail = principal.getName();
        Student student = studentService.findStudentByEmail(loggedInUserEmail);
        result.setStudent(student);

        if (result.getStudentAnswers() != null) {
            result.getStudentAnswers().forEach(answer -> {
                answer.setResult(result);
            });
        }
        if (result.getDateTaken() == null) {
            // Set the time ONLY if the client did not provide one
            result.setDateTaken(LocalDateTime.now());
        }

        // 1. Save the entity
        StudentTestResult savedResult = resultRepository.save(result);

        // 2. Map essential data to the response DTO
        TestResultResponseDTO responseDto = new TestResultResponseDTO();
        responseDto.setResultId(savedResult.getId());
        responseDto.setStudentId(savedResult.getStudent().getId());
        responseDto.setTestId(savedResult.getTest().getId());

        // 3. Return a clean 201 Created status with the simple DTO
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }
    // Inside StudentTestResultController.java

    @PutMapping("/{id}")
    public ResponseEntity<TestResultDTO> updateResult(@PathVariable Long id, @RequestBody StudentTestResult updatedResult) {
        return resultRepository.findById(id)
                .map(existingResult -> {
                    existingResult.setScore(updatedResult.getScore());
                    existingResult.setTotalQuestions(updatedResult.getTotalQuestions());

                    StudentTestResult savedEntity = resultRepository.save(existingResult);

                    return ResponseEntity.ok(resultMapper.toDto(savedEntity));
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // Handle 404 if ID not found
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (!resultRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        resultRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}