package com.elearning.backend.security.service;
import com.elearning.backend.model.Admin;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.repository.AdminRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository; // NEW: Injected Admin Repository

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // --- 1. PRIORITY CHECK: ADMIN ---
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return admin.get();
        }
        // 1. Try to find the user as a STUDENT
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return student.get();
        }

        // 2. If not a student, try to find the user as a TEACHER
        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            return teacher.get();
        }

        // 3. If no user is found, throw the standard Spring Security exception
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}