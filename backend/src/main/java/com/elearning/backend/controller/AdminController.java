package com.elearning.backend.controller;

import com.elearning.backend.model.Student;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.service.StudentService; // Assuming you need this for hashing
import com.elearning.backend.service.TeacherService; // Assuming you need this for hashing
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    private final StudentService studentService;
    private final TeacherService teacherService;

    private final PasswordEncoder passwordEncoder;


    // =========================================================================
    // API 1: GET /api/admin/stats (Analytics Dashboard)
    // =========================================================================

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Enforce the highest security
    public ResponseEntity<Map<String, Long>> getStats() {

        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/teachers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        // Delegate to the service that handles hashing and saving
        Teacher savedTeacher = teacherService.registerNewTeacher(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeacher);
    }


    @DeleteMapping("/teachers/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

   @PostMapping("/students")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public ResponseEntity<Student> createStudent(@RequestBody Student student) {
         // Delegate to the service that handles hashing and saving
         Student savedStudent = studentService.registerNewStudent(student);
         return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
   }
    @DeleteMapping("/students/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}