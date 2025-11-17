package com.elearning.backend.service;

import com.elearning.backend.dto.VideoLectureDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.model.VideoLecture;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoLectureService {

    private final Path rootDir = Paths.get("uploads/video_lectures").toAbsolutePath().normalize();

    private final VideoLectureRepository videoLectureRepository;
    private final TeacherRepository teacherRepository;
    private final VideoLectureMapper videoLectureMapper;
    private final StudentRepository studentRepository;

    public VideoLecture uploadVideo(String teacherEmail, String title, String duration, MultipartFile file,
                                    int targetStandard, String subject) throws IOException {

        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with Email: " + teacherEmail));

        if (!Files.exists(rootDir)) {
            Files.createDirectories(rootDir);
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path targetLocation = this.rootDir.resolve(uniqueFilename);

        Files.copy(file.getInputStream(), targetLocation);

        VideoLecture lecture = new VideoLecture();
        lecture.setTitle(title);
        lecture.setDuration(duration);
        lecture.setFilePath(targetLocation.toString());
        lecture.setTeacher(teacher);
        lecture.setTargetStandard(targetStandard);
        lecture.setSubject(subject);

        return videoLectureRepository.save(lecture);
    }

    public List<VideoLectureDTO> getVideosForStudent(
            String studentEmail, String searchSubject) {

        // 1. Fetch the Student entity using the secure email identifier (Authorization)
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for email: " + studentEmail));

        Integer studentStandard = student.getStandard();

        // 2. Perform the secure filtering and search query using the repository method
        List<VideoLecture> entities = videoLectureRepository
                .findByTargetStandardAndSubjectContainingIgnoreCase(
                        studentStandard,
                        searchSubject
                );

        // 3. Map entities to DTOs and return
        return entities.stream()
                .map(videoLectureMapper::toDto)
                .collect(Collectors.toList());
    }


    public Resource downloadVideo(Long lectureId) {

        VideoLecture lecture = videoLectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Video Lecture not found with ID: " + lectureId));

        Path filePath = Paths.get(lecture.getFilePath()).toAbsolutePath().normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on server for lecture ID: " + lectureId);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file path: " + lecture.getFilePath(), e);
        }
    }
    public Resource downloadVideoSecurely(String studentEmail, Long lectureId) throws AccessDeniedException { // <-- REMOVED studentId parameter

        // 1. Fetch Student by Email (Username)
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for email: " + studentEmail));

        // 2. Fetch Lecture
        VideoLecture lecture = videoLectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Video Lecture not found with ID: " + lectureId));

        // 3. AUTHORIZATION CHECK (Logic remains the same)
        if (student.getStandard() != lecture.getTargetStandard()) {
            throw new AccessDeniedException("Access denied. Lecture is not intended for Standard " + student.getStandard());
        }

        // 3. File access logic (similar to the previous downloadVideo method)
        Path filePath = Paths.get(lecture.getFilePath()).toAbsolutePath().normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on server for lecture ID: " + lectureId);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file path: " + lecture.getFilePath(), e);
        }
    }

    public List<VideoLecture> getVideosByTeacher(Long teacherId) {
        return videoLectureRepository.findByTeacherId(teacherId);
    }
}