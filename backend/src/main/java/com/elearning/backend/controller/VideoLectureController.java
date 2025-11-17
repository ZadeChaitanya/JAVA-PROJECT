package com.elearning.backend.controller;

import com.elearning.backend.dto.VideoLectureDTO; // New DTO Import
import com.elearning.backend.model.VideoLecture;
import com.elearning.backend.service.VideoLectureService;
import com.elearning.backend.service.VideoLectureMapper; // New Mapper Import
import lombok.RequiredArgsConstructor; // For clean constructor injection
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lectures")
@CrossOrigin("*")
@RequiredArgsConstructor // Automatically injects final fields
public class VideoLectureController {

    private final VideoLectureService videoLectureService;
    private final VideoLectureMapper videoLectureMapper; // <-- New Injected Dependency

    // 1. UPLOAD ENDPOINT
    @PostMapping("/upload") // <-- REMOVED {teacherId} from the path
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")// <-- NEW: Only teachers can access
    public ResponseEntity<VideoLecture> uploadLecture(
            Principal principal, // <-- Get the identity of the logged-in user
            @RequestParam("title") String title,
            @RequestParam("duration") String duration,
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetStandard") int targetStandard,
            @RequestParam("subject") String subject

        ) throws IOException {

        // Logic must be updated: Get the Teacher's email (username) from the token
        String teacherEmail = principal.getName();

        // You now need a service method that accepts the email/username instead of the ID
        VideoLecture lecture = videoLectureService.uploadVideo(teacherEmail, title, duration, file, targetStandard, subject);
        return ResponseEntity.status(201).body(lecture);
    }

    // 2. DOWNLOAD ENDPOINT (No change needed, as it returns a Resource)
    @GetMapping("/download/{lectureId}")
    public ResponseEntity<Resource> downloadLecture(@PathVariable Long lectureId) {
        Resource resource = videoLectureService.downloadVideo(lectureId);

        String mimeType = "video/mp4";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @GetMapping("/stream/{lectureId}")
    public ResponseEntity<Resource> streamLecture(@PathVariable Long lectureId) {
        Resource resource = videoLectureService.downloadVideo(lectureId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }

    // 3. GET BY TEACHER ENDPOINT (Updated to return DTOs)
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<VideoLectureDTO>> getLecturesByTeacher(@PathVariable Long teacherId) {

        // 1. Fetch the list of ENITITIES
        List<VideoLecture> entities = videoLectureService.getVideosByTeacher(teacherId);

        // 2. Map the list of ENTITIES to a list of DTOs
        List<VideoLectureDTO> dtos = entities.stream()
                .map(videoLectureMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos); // Return DTO list
    }

    @GetMapping("/student/content") // <-- FIX 1: SIMPLIFIED URL PATH (no ID needed)
    public ResponseEntity<List<VideoLectureDTO>> getStudentLectures(
            Principal principal, // <-- FIX 2: GETS USER EMAIL FROM JWT TOKEN
            @RequestParam(required = false, defaultValue = "") String subject
    ) {
        // Delegate secure filtering, using the email (username) from the token
        List<VideoLectureDTO> dtos = videoLectureService.getVideosForStudent(
                principal.getName(), // principal.getName() is the user's email/username
                subject
        );
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/secure/download/{lectureId}")
    public ResponseEntity<Resource> secureDownloadLecture(
            Principal principal, // <-- Get JWT subject (email) directly
            @PathVariable Long lectureId
    ) throws AccessDeniedException {
        // Calls the service method with the authorization check
        Resource resource = videoLectureService.downloadVideoSecurely(principal.getName(), lectureId);

        // Forces download (Secure)
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/secure/stream/{lectureId}")
    public ResponseEntity<Resource> secureStreamLecture(
            Principal principal, // <-- Get JWT subject (email) directly
            @PathVariable Long lectureId
    ) throws AccessDeniedException {
        // We pass the email (principal.getName()) to the service layer for lookup
        Resource resource = videoLectureService.downloadVideoSecurely(principal.getName(), lectureId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }
}