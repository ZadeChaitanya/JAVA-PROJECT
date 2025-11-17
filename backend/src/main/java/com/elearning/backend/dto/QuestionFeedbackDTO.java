package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionFeedbackDTO {
    private Long questionId;
    private int questionIndex;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String studentAnswer; // The answer the student submitted
    private String correctAnswer; // The true correct answer
    private boolean isCorrect;    // True if studentAnswer == correctAnswer

    public void setIsCorrect(boolean b) {
        this.isCorrect = b;
    }
}