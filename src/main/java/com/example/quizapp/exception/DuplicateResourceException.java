package com.example.quizapp.exception;

/**
 * Thrown when attempting to create a resource that violates a uniqueness constraint
 * (e.g. a category name or user email that already exists).
 * Mapped to HTTP 409 by GlobalExceptionHandler.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
