package com.elearning.backend.repository;

import com.elearning.backend.model.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    // 1. Check if a specific content item is already tracked/completed by a specific student
    Optional<StudentProgress> findByStudentIdAndContentIdAndContentType(
            Long studentId, Long contentId, String contentType);

    // 2. Get all progress items for the logged-in student (for status API)
    List<StudentProgress> findByStudentId(Long studentId);
}