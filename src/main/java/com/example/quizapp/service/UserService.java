package com.example.quizapp.service;

import com.example.quizapp.dto.user.UserRequest;
import com.example.quizapp.dto.user.UserResponse;
import com.example.quizapp.entity.User;
import com.example.quizapp.exception.DuplicateResourceException;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Manages application users.
 *
 * NOTE ON AUTHENTICATION: To keep the first version simple and unblock
 * frontend integration quickly, this service does NOT implement full
 * Spring Security / JWT authentication. Passwords are stored as-is for now
 * (never returned in any response) and admin vs. user APIs are separated
 * purely by URL path (/api/admin/** vs /api/**). This layer is intentionally
 * isolated behind UserService/UserController so that real authentication
 * (Spring Security + JWT + BCrypt password hashing) can be dropped in later
 * without changing any other part of the application.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("A user with email '" + request.getEmail() + "' already exists");
        }

        User.Role role = User.Role.USER;
        if (StringUtils.hasText(request.getRole())) {
            try {
                role = User.Role.valueOf(request.getRole().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("role must be either ADMIN or USER");
            }
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(role)
                .build();

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findUserOrThrow(id));
    }

    protected User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
