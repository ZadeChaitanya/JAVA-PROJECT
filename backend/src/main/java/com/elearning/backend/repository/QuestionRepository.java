package com.elearning.backend.repository;
import com.elearning.backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTestId(Long testId);
    @Query
            ("SELECT MAX(q.questionIndex) FROM Question q WHERE q.test.id = :testId")
    Integer findMaxQuestionIndexByTestId(@Param("testId") Long testId);
}