package com.example.quizapp.controller;

import com.example.quizapp.dto.category.CategoryResponse;
import com.example.quizapp.dto.question.UserQuestionResponse;
import com.example.quizapp.service.CategoryService;
import com.example.quizapp.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public/user-facing category & question browsing APIs.
 * Base path: /api/categories
 *
 * These endpoints are read-only and never expose the correct answer for a question.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final QuestionService questionService;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * Returns EVERY question currently stored under this category.
     * If the category has 5 questions, 5 are returned. If it has 50, all 50 are returned.
     * correctAnswer is never included in the response.
     */
    @GetMapping("/{categoryId}/questions")
    public List<UserQuestionResponse> getQuestionsForCategory(@PathVariable Long categoryId) {
        return questionService.getQuestionsByCategoryForUser(categoryId);
    }
}
