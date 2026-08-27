package com.example.quizapp.controller;

import com.example.quizapp.dto.quiz.QuizAttemptResponse;
import com.example.quizapp.dto.quiz.QuizResultResponse;
import com.example.quizapp.dto.quiz.QuizSubmissionRequest;
import com.example.quizapp.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Quiz submission & result retrieval APIs.
 * Base paths: /api/quizzes, /api/quiz-attempts, /api/users/{userId}/quiz-attempts
 */
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * Submits a completed (or partially completed) quiz attempt for grading.
     * Grades against ALL questions currently stored for the category - no hardcoded count.
     */
    @PostMapping("/api/quizzes/{categoryId}/submit")
    public QuizResultResponse submitQuiz(
            @PathVariable Long categoryId,
            @Valid @RequestBody QuizSubmissionRequest request) {
        return quizService.submitQuiz(categoryId, request);
    }

    @GetMapping("/api/quiz-attempts/{attemptId}")
    public QuizResultResponse getAttemptById(@PathVariable Long attemptId) {
        return quizService.getAttemptById(attemptId);
    }

    @GetMapping("/api/users/{userId}/quiz-attempts")
    public List<QuizAttemptResponse> getAttemptsForUser(@PathVariable Long userId) {
        return quizService.getAttemptsForUser(userId);
    }
}
