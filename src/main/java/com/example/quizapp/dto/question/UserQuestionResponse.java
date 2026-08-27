package com.example.quizapp.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User-facing question representation.
 * IMPORTANT: This class intentionally has NO correctAnswer field.
 * It must never be added here - the correct answer must remain hidden from end users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuestionResponse {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
}
