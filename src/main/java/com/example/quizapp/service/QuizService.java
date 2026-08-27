package com.example.quizapp.service;

import com.example.quizapp.dto.quiz.AnswerRequest;
import com.example.quizapp.dto.quiz.QuizAttemptResponse;
import com.example.quizapp.dto.quiz.QuizResultResponse;
import com.example.quizapp.dto.quiz.QuizSubmissionRequest;
import com.example.quizapp.entity.Category;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuizAttempt;
import com.example.quizapp.entity.User;
import com.example.quizapp.exception.InvalidQuizSubmissionException;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.CategoryRepository;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizAttemptRepository;
import com.example.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core quiz engine: validates submissions, grades them against the category's
 * FULL question set (however many questions that happens to be - no hardcoded
 * count anywhere), persists the attempt, and returns the computed result.
 *
 * SCORING RULES (documented per spec section 15/16):
 *  - totalQuestions = number of questions currently stored for the category
 *    (NOT just the number the user answered).
 *  - Each correct answer = 1 mark. score = correctAnswers.
 *  - Any question the user did not submit an answer for (or left blank) is
 *    treated as an "unanswered" question and is counted towards wrongAnswers
 *    as well, i.e. wrongAnswers = totalQuestions - correctAnswers, and
 *    unanswered is reported separately for transparency.
 *  - percentage = (correctAnswers / totalQuestions) * 100, computed with
 *    floating point division to avoid truncation.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizResultResponse submitQuiz(Long categoryId, QuizSubmissionRequest request) {

        // 1. Verify the category exists.
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        // Verify the user exists.
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // The full, current question set for this category - however many that is.
        List<Question> categoryQuestions = questionRepository.findByCategoryId(categoryId);
        if (categoryQuestions.isEmpty()) {
            throw new InvalidQuizSubmissionException("Category '" + category.getName() + "' has no questions to quiz on");
        }

        Map<Long, Question> questionsById = new HashMap<>();
        for (Question q : categoryQuestions) {
            questionsById.put(q.getId(), q);
        }

        // 2 & 3. Find all submitted questions and verify each belongs to the selected category.
        Map<Long, String> submittedAnswers = new HashMap<>();
        for (AnswerRequest answer : request.getAnswers()) {
            Question question = questionsById.get(answer.getQuestionId());
            if (question == null) {
                // Either the question doesn't exist at all, or it exists but belongs to
                // a different category - both are invalid submissions for this quiz.
                Question anyQuestion = questionRepository.findById(answer.getQuestionId()).orElse(null);
                if (anyQuestion == null) {
                    throw new InvalidQuizSubmissionException(
                            "Question not found with id: " + answer.getQuestionId());
                }
                throw new InvalidQuizSubmissionException(
                        "Question id " + answer.getQuestionId() + " does not belong to category '"
                                + category.getName() + "'");
            }
            submittedAnswers.put(answer.getQuestionId(), answer.getSelectedAnswer());
        }

        // 4-9. Grade against the FULL question set for the category.
        int totalQuestions = categoryQuestions.size();
        int correctAnswers = 0;
        int unanswered = 0;

        for (Question question : categoryQuestions) {
            String selected = submittedAnswers.get(question.getId());
            if (selected == null || selected.isBlank()) {
                unanswered++;
                continue;
            }
            if (selected.equalsIgnoreCase(question.getCorrectAnswer())) {
                correctAnswers++;
            }
        }

        // Unanswered questions are treated as wrong (documented behavior).
        int wrongAnswers = totalQuestions - correctAnswers;
        int score = correctAnswers;
        double percentage = (correctAnswers * 100.0) / totalQuestions;
        percentage = Math.round(percentage * 100.0) / 100.0; // round to 2 decimal places

        // 10. Save the QuizAttempt.
        QuizAttempt attempt = QuizAttempt.builder()
                .userId(user.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .wrongAnswers(wrongAnswers)
                .unanswered(unanswered)
                .score(score)
                .percentage(percentage)
                .build();

        QuizAttempt saved = quizAttemptRepository.save(attempt);

        // 11. Return the result.
        return toResultResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptResponse> getAttemptsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(userId).stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuizResultResponse getAttemptById(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found with id: " + attemptId));
        return toResultResponse(attempt);
    }

    private QuizResultResponse toResultResponse(QuizAttempt attempt) {
        return QuizResultResponse.builder()
                .attemptId(attempt.getId())
                .userId(attempt.getUserId())
                .categoryId(attempt.getCategoryId())
                .categoryName(attempt.getCategoryName())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .wrongAnswers(attempt.getWrongAnswers())
                .unanswered(attempt.getUnanswered())
                .score(attempt.getScore())
                .percentage(attempt.getPercentage())
                .attemptedAt(attempt.getAttemptedAt())
                .build();
    }

    private QuizAttemptResponse toAttemptResponse(QuizAttempt attempt) {
        return QuizAttemptResponse.builder()
                .attemptId(attempt.getId())
                .categoryId(attempt.getCategoryId())
                .categoryName(attempt.getCategoryName())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .wrongAnswers(attempt.getWrongAnswers())
                .unanswered(attempt.getUnanswered())
                .score(attempt.getScore())
                .percentage(attempt.getPercentage())
                .attemptedAt(attempt.getAttemptedAt())
                .build();
    }
}
