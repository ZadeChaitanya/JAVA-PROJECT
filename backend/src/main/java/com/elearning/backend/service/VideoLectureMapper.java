package com.elearning.backend.service;

import com.elearning.backend.dto.VideoLectureDTO;
import com.elearning.backend.model.VideoLecture;
import org.springframework.stereotype.Service;

@Service
public class VideoLectureMapper {

    public VideoLectureDTO toDto(VideoLecture entity) {
        VideoLectureDTO dto = new VideoLectureDTO();

        // Map simple fields
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDuration(entity.getDuration());
        dto.setUploadDate(entity.getUploadDate());

        // Map flattened Teacher fields
        if (entity.getTeacher() != null) {
            // Accessing the Teacher here is safe because the LAZY proxy will be initialized
            // inside the transactional boundary (Controller -> Service -> Repo).
            dto.setTeacherId(entity.getTeacher().getId());
            dto.setTeacherName(entity.getTeacher().getName());
        }

        return dto;
    }
}