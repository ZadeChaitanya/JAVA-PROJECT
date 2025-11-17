package com.elearning.backend.service;

import com.elearning.backend.dto.ProgressStatusDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentProgress;
import com.elearning.backend.model.StudyMaterial;
import com.elearning.backend.model.VideoLecture;
import com.elearning.backend.repository.StudentProgressRepository;
import com.elearning.backend.repository.StudyMaterialRepository;
import com.elearning.backend.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final StudentProgressRepository studentProgressRepository;
    private final StudentService studentService; // Used to fetch the authenticated student
    private final VideoLectureRepository videoLectureRepository;
    private final StudyMaterialRepository studyMaterialRepository;

    /**
     * Marks a specific piece of content (video or material) as completed.
     * If the record exists, it updates the status; otherwise, it creates a new record.
     */
    public StudentProgress markContentComplete(String studentEmail, Long contentId, String contentType) {

        // 1. Get the authenticated Student entity
        Student student = studentService.findStudentByEmail(studentEmail);
        Long studentId = student.getId();

        // 2. Check if progress already exists
        Optional<StudentProgress> existingProgress =
                studentProgressRepository.findByStudentIdAndContentIdAndContentType(
                        studentId, contentId, contentType
                );

        StudentProgress progress;

        if (existingProgress.isPresent()) {
            // Case A: Update existing record
            progress = existingProgress.get();
        } else {
            // Case B: Create new record
            progress = new StudentProgress();
            progress.setStudent(student);
            progress.setContentId(contentId);
            progress.setContentType(contentType);
        }

        // 3. Set completion status and timestamp
        progress.setCompleted(true);
        progress.setCompletionDate(LocalDateTime.now());

        // 4. Save and return
        return studentProgressRepository.save(progress);
    }
    public List<ProgressStatusDTO> getProgressStatus(String studentEmail) {

        // 1. Get the authenticated Student entity
        Student student = studentService.findStudentByEmail(studentEmail);
        Long studentId = student.getId();

        // 2. Fetch all progress records for this student
        List<StudentProgress> progressEntities = studentProgressRepository.findByStudentId(studentId);

        // 3. Map entities to DTOs, joining content metadata
        return progressEntities.stream()
                .map(this::mapToProgressStatusDTO) // Use a helper method for clarity
                .collect(Collectors.toList());
    }
    private ProgressStatusDTO mapToProgressStatusDTO(StudentProgress progress) {
        ProgressStatusDTO dto = new ProgressStatusDTO();

        dto.setContentId(progress.getContentId());
        dto.setContentType(progress.getContentType());
        dto.setIsCompleted(progress.isCompleted());
        dto.setCompletionDate(progress.getCompletionDate());

        // --- Dynamic Content Lookup ---
        if ("LECTURE".equals(progress.getContentType())) {
            Optional<VideoLecture> lecture = videoLectureRepository.findById(progress.getContentId());
            lecture.ifPresent(l -> {
                dto.setTitle(l.getTitle());
                dto.setSubject(l.getSubject());
            });
        } else if ("MATERIAL".equals(progress.getContentType())) {
            Optional<StudyMaterial> material = studyMaterialRepository.findById(progress.getContentId());
            material.ifPresent(m -> {
                dto.setTitle(m.getTitle());
                dto.setSubject(m.getSubject());
            });
        }

        return dto;
    }
}