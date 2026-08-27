package com.example.quizapp.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotEmpty(message = "answers list cannot be empty")
    @Valid
    private List<AnswerRequest> answers;
}
