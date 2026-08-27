package com.example.quizapp.dto.quiz;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    @NotNull(message = "questionId is required")
    private Long questionId;

    // Optional - a question may legitimately be left unanswered.
    // When present, must be A, B, C or D.
    @Pattern(regexp = "^[A-Da-d]$", message = "selectedAnswer must be one of A, B, C or D")
    private String selectedAnswer;
}
