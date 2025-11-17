package com.elearning.backend.repository;
import com.elearning.backend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Custom query methods
    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByTeacherId(Long teacherId);

    List<Attendance> findByDate(LocalDate date);
}