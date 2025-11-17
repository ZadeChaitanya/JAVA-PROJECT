package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student_answers")
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the specific Test Result
    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private StudentTestResult result;

    // Link to the question being answered
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // The student's submitted choice
    private String submittedAnswer; // e.g., "A", "B", "C"
}