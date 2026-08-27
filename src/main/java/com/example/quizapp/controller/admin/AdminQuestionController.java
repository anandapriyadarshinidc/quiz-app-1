package com.example.quizapp.controller.admin;

import com.example.quizapp.dto.question.AdminQuestionResponse;
import com.example.quizapp.dto.question.QuestionRequest;
import com.example.quizapp.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only question management APIs.
 * Base path: /api/admin/questions
 *
 * There is NO limit imposed here on how many questions can be added to a category -
 * createQuestion can be called any number of times against the same categoryId.
 */
@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<AdminQuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        AdminQuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AdminQuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminQuestionResponse> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AdminQuestionResponse>> getQuestionsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(questionService.getQuestionsByCategoryForAdmin(categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminQuestionResponse> updateQuestion(
            @PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
