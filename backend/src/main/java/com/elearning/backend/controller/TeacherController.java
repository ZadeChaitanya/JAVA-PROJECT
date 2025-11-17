package com.elearning.backend.controller;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "http://localhost:3000") // frontend allowed later
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return ResponseEntity.ok(teacher);
    }
    @PostMapping
    public Teacher addTeacher(@RequestBody Teacher teacher) {
        // Delegate to the service, which handles hashing and saving
        return teacherService.registerNewTeacher(teacher);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacherDetails) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        // Update the fields (assuming Teacher model has setters for all necessary fields)
        teacher.setName(teacherDetails.getName());
        teacher.setEmail(teacherDetails.getEmail());
        teacher.setSubject(teacherDetails.getSubject());
        // Note: Password update should ideally be handled via a separate, secure endpoint.

        Teacher updatedTeacher = teacherRepository.save(teacher);
        return ResponseEntity.ok(updatedTeacher);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {

        // Find the teacher to ensure it exists before deleting
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacherRepository.delete(teacher);

        // Return 200 OK or 204 No Content
        return ResponseEntity.ok().build();
    }
}
