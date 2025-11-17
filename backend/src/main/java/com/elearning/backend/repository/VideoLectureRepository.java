package com.elearning.backend.repository;

import com.elearning.backend.model.VideoLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoLectureRepository extends JpaRepository<VideoLecture, Long> {

    // Method to get videos by teacher ID (e.g., for a teacher's dashboard)
    List<VideoLecture> findByTeacherId(Long teacherId);
    List<VideoLecture> findByTeacherIdAndTargetStandardAndSubjectContainingIgnoreCase(
            Long teacherId, int targetStandard, String subject);
    List<VideoLecture> findByTargetStandardAndSubjectContainingIgnoreCase(
            int targetStandard, String subject);
}