package com.elearning.backend.service;

import com.elearning.backend.dto.StudyMaterialDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudyMaterial;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.StudyMaterialRepository;
import com.elearning.backend.model.Teacher; // Required for fetching Teacher during upload
import com.elearning.backend.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import org.springframework.security.access.AccessDeniedException; // For security checks

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMaterialService {

    private final Path rootDir = Paths.get("uploads/study_materials").toAbsolutePath().normalize();

    // Injected Dependencies
    private final StudyMaterialRepository materialRepository;
    private final StudentRepository studentRepository;
    private final StudyMaterialMapper materialMapper;
    private final TeacherRepository teacherRepository; // Assuming you need this for upload

    // =========================================================================
    // UPLOAD LOGIC (Set Standard & Subject)
    // =========================================================================

    public StudyMaterial uploadMaterial(
            String teacherEmail,
            String title,
            MultipartFile file,
            int targetStandard,
            String subject
    ) throws IOException {

        // Fetch Teacher
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + teacherEmail));

        // File System Logic (Ensure Directory Exists)
        if (!Files.exists(rootDir)) {
            Files.createDirectories(rootDir);
        }

        // Save File
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path targetLocation = this.rootDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation);

        // Create and Save Entity
        StudyMaterial material = new StudyMaterial();
        material.setTitle(title);
        material.setFileType(fileExtension);
        material.setFilePath("uploads/study_materials/" + uniqueFilename);
        material.setUploadDate(LocalDateTime.now());
        material.setTeacher(teacher);
        material.setTargetStandard(targetStandard); // Set access control
        material.setSubject(subject);               // Set search metadata

        return materialRepository.save(material);
    }

    // =========================================================================
    // GET FOR STUDENT (SECURE FILTERING LOGIC)
    // =========================================================================

    public List<StudyMaterialDTO> getMaterialsForStudent(String studentEmail, String searchSubject) {

        // 1. Fetch Student entity using the secure email identifier
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for email: " + studentEmail));

        // Authorization and Filtering logic remains the same:
        Integer studentStandard = student.getStandard();

        List<StudyMaterial> entities = materialRepository
                .findByTargetStandardAndSubjectContainingIgnoreCase(
                        studentStandard,
                        searchSubject
                );

        // Map and return DTOs
        return entities.stream()
                .map(materialMapper::toDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // DOWNLOAD LOGIC (SECURE)
    // =========================================================================

    public Resource downloadMaterialSecurely(String studentEmail, Long materialId) throws AccessDeniedException {

        // 1. Fetch Student and Material
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for email: " + studentEmail));
        StudyMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Material not found with ID: " + materialId));

        // 2. AUTHORIZATION CHECK
        if (student.getStandard() != material.getTargetStandard()) {
            throw new AccessDeniedException("Access denied. Material is not intended for Standard " + student.getStandard());
        }

        // 3. File access logic
        Path filePath = Paths.get(material.getFilePath()).toAbsolutePath().normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on server for material ID: " + materialId);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file path: " + material.getFilePath(), e);
        }
    }
    public Resource downloadMaterialUnsecured(Long materialId) {
        // 1. Fetch the entity to get the stored file path
        StudyMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Material not found with ID: " + materialId));

        // 2. File access logic (same as the secure version, but without student check)
        Path filePath = Paths.get(material.getFilePath()).toAbsolutePath().normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on server for material ID: " + materialId);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file path: " + material.getFilePath(), e);
        }
    }

    public List<StudyMaterial> getMaterialsByTeacher(Long teacherId) {
        return materialRepository.findByTeacherId(teacherId);
    }
}