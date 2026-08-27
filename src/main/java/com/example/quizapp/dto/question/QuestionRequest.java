package com.example.quizapp.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "questionText is required")
    private String questionText;

    @NotBlank(message = "optionA is required")
    private String optionA;

    @NotBlank(message = "optionB is required")
    private String optionB;

    @NotBlank(message = "optionC is required")
    private String optionC;

    @NotBlank(message = "optionD is required")
    private String optionD;

    @NotBlank(message = "correctAnswer is required")
    @Pattern(regexp = "^[A-Da-d]$", message = "correctAnswer must be one of A, B, C or D")
    private String correctAnswer;

    @NotNull(message = "categoryId is required")
    private Long categoryId;
}
