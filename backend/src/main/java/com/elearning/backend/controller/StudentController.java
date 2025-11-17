package com.elearning.backend.controller;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
//import org.springframework.security.crypto.password.PasswordEncoder;
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173") // allow frontend later


public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        // Delegate the entire save operation to a new service method
        return studentService.registerNewStudent(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {

        // Find the existing student or throw 404 (using the assumed exception)
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Update the fields (assuming Student model has getters/setters for all necessary fields)
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setStandard(studentDetails.getStandard());
        // ... (update other required fields)

        // Save and return the updated student
        Student updatedStudent = studentRepository.save(student);
        return ResponseEntity.ok(updatedStudent);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {

        // Find the student to ensure it exists before deleting
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        studentRepository.delete(student);

        // Return 200 OK or 204 No Content (commonly used for delete)
        return ResponseEntity.ok().build();
    }
}

