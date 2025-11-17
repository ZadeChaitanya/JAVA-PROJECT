package com.elearning.backend.service;

import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder; // Injected here

    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public Student findStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated student not found."));
    }
    public Student registerNewStudent(Student student) {
        String rawPassword = student.getPassword();

        // **CRUCIAL CHECK:** If the password is null after Jackson binds the JSON,
        // the application MUST throw an error here, but we now isolate the crash.
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // Hash the password
        student.setPassword(passwordEncoder.encode(rawPassword));

        return studentRepository.save(student);
    }
}