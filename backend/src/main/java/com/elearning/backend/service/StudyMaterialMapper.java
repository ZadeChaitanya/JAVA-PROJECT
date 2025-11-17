package com.elearning.backend.service;

import com.elearning.backend.dto.StudyMaterialDTO;
import com.elearning.backend.model.StudyMaterial;
import org.springframework.stereotype.Service;

@Service
public class StudyMaterialMapper {


    public StudyMaterialDTO toDto(StudyMaterial entity) {
        StudyMaterialDTO dto = new StudyMaterialDTO();

        // Map main fields
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setFileType(entity.getFileType());
        dto.setUploadDate(entity.getUploadDate());

        // Map new fields
        dto.setTargetStandard(entity.getTargetStandard());
        dto.setSubject(entity.getSubject());

        // Map flattened Teacher fields
        if (entity.getTeacher() != null) {
            // Accessing nested entity fields to populate the DTO
            dto.setTeacherId(entity.getTeacher().getId());
            dto.setTeacherName(entity.getTeacher().getName());
        }

        return dto;
    }

    // (A method to convert List<Entity> to List<DTO> could also be added here)
}