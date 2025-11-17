package com.elearning.backend.repository;
import com.elearning.backend.model.StudentTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentTestResultRepository extends JpaRepository<StudentTestResult, Long> {
    List<StudentTestResult> findByStudentId(Long studentId);
    List<StudentTestResult> findByTestId(Long testId);
}
