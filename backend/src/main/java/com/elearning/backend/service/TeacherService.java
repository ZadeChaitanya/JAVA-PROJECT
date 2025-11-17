package com.elearning.backend.service;

import com.elearning.backend.model.Teacher;
import com.elearning.backend.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public Teacher registerNewTeacher(Teacher teacher) {
        String rawPassword = teacher.getPassword();

        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password is required for teacher registration.");
        }

        // Hash the password
        teacher.setPassword(passwordEncoder.encode(rawPassword));

        return teacherRepository.save(teacher);
    }

    // You can add other teacher-related business logic here later
}