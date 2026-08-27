package com.example.quizapp.service;

import com.example.quizapp.dto.question.AdminQuestionResponse;
import com.example.quizapp.dto.question.QuestionRequest;
import com.example.quizapp.dto.question.UserQuestionResponse;
import com.example.quizapp.entity.Category;
import com.example.quizapp.entity.Question;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.CategoryRepository;
import com.example.quizapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles all question CRUD operations.
 * IMPORTANT: There is no limit anywhere in this service on how many questions
 * a single category may have - findByCategoryId always returns the full list.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    public AdminQuestionResponse createQuestion(QuestionRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctAnswer(request.getCorrectAnswer().toUpperCase())
                .category(category)
                .build();

        Question saved = questionRepository.save(question);
        return toAdminResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AdminQuestionResponse> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminQuestionResponse getQuestionById(Long id) {
        Question question = findQuestionOrThrow(id);
        return toAdminResponse(question);
    }

    /**
     * Admin view of every question in a category (includes correct answers).
     * Returns ALL questions - no pagination, no fixed size limit.
     */
    @Transactional(readOnly = true)
    public List<AdminQuestionResponse> getQuestionsByCategoryForAdmin(Long categoryId) {
        ensureCategoryExists(categoryId);
        return questionRepository.findByCategoryId(categoryId).stream()
                .map(this::toAdminResponse)
                .toList();
    }

    /**
     * User view of every question in a category (correct answer hidden).
     * Returns ALL questions currently stored for the category.
     */
    @Transactional(readOnly = true)
    public List<UserQuestionResponse> getQuestionsByCategoryForUser(Long categoryId) {
        ensureCategoryExists(categoryId);
        return questionRepository.findByCategoryId(categoryId).stream()
                .map(this::toUserResponse)
                .toList();
    }

    public AdminQuestionResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = findQuestionOrThrow(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer().toUpperCase());
        question.setCategory(category);

        Question saved = questionRepository.save(question);
        return toAdminResponse(saved);
    }

    public void deleteQuestion(Long id) {
        Question question = findQuestionOrThrow(id);
        questionRepository.delete(question);
    }

    protected Question findQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    private void ensureCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
    }

    private AdminQuestionResponse toAdminResponse(Question q) {
        return AdminQuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .correctAnswer(q.getCorrectAnswer())
                .categoryId(q.getCategory().getId())
                .categoryName(q.getCategory().getName())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }

    private UserQuestionResponse toUserResponse(Question q) {
        // correctAnswer deliberately omitted
        return UserQuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .build();
    }
}
