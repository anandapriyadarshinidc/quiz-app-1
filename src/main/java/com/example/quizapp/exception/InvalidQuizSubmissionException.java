package com.example.quizapp.exception;

/**
 * Thrown when a quiz submission is malformed - e.g. a submitted question
 * does not belong to the selected category, or contains an invalid answer option.
 * Mapped to HTTP 400 by GlobalExceptionHandler.
 */
public class InvalidQuizSubmissionException extends RuntimeException {
    public InvalidQuizSubmissionException(String message) {
        super(message);
    }
}
