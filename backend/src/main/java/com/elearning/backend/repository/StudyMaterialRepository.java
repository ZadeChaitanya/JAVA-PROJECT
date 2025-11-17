package com.elearning.backend.repository;
import com.elearning.backend.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findByTeacherId(Long teacherId);
    List<StudyMaterial> findByTeacherIdAndTargetStandardAndSubjectContainingIgnoreCase(
            Long teacherId, int targetStandard, String subject);

    List<StudyMaterial> findByTargetStandardAndSubjectContainingIgnoreCase(
            int targetStandard, String subject);
}
