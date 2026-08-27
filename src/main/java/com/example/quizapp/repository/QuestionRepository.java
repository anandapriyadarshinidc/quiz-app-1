package com.example.quizapp.repository;

import com.example.quizapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // No pagination/limit applied - always returns ALL questions for the category.
    List<Question> findByCategoryId(Long categoryId);

    List<Question> findByIdIn(List<Long> ids);

    long countByCategoryId(Long categoryId);

    void deleteByCategoryId(Long categoryId);
}
