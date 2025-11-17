package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor; // Required for constructor with all arguments
import lombok.Data;              // Provides Getters, Setters, toString, hashCode, and equals
import lombok.NoArgsConstructor;   // Provides the default constructor (required by JPA/Jackson)
import lombok.experimental.Accessors; // Optional, can be used for chaining setters

@Entity
@Table(name = "questions")
@Data                           // Generates all getters/setters/toString/hashCode/equals
@NoArgsConstructor              // Generates the public no-args constructor
@AllArgsConstructor             // Generates the constructor with all fields
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "integer default 0")
    private int questionIndex;

    // @Setter is no longer needed here as @Data provides setters for all fields
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "test_id")
    @JsonBackReference
    private Test test;

    // NOTE: The previous constructor that omitted 'id' and 'questionIndex' is replaced by
    // the @AllArgsConstructor. If you only want a constructor that matches the old one,
    // you must manually define it, or use @Builder instead of @AllArgsConstructor.
}