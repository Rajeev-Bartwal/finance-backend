package com.finance.services;

import com.finance.dto.request.SetStatusRequest;
import com.finance.dto.request.UpdateUserRequest;
import com.finance.dto.response.PagedResponse;
import com.finance.dto.response.UserResponse;
import com.finance.exception.DuplicateResourceException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.models.User;
import com.finance.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public PagedResponse<UserResponse> getAll(int page, int size) {
        Page<UserResponse> result = userRepository
                .findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()))
                .map(UserResponse::from);
        return new PagedResponse<>(result);
    }

    public UserResponse getById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    public UserResponse getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);

        if (request.getUsername() != null) {
            if (userRepository.existsByUsername(request.getUsername())
                    && !user.getUsername().equals(request.getUsername())) {
                throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail())
                    && !user.getEmail().equals(request.getEmail())) {
                throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse setStatus(Long id, SetStatusRequest request) {
        User user = findOrThrow(id);
        user.setActive(request.getActive());
        userRepository.save(user);
        log.info("User {} status set to active={}", user.getUsername(), request.getActive());
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findOrThrow(id);
        userRepository.delete(user);
        log.info("User {} soft-deleted", user.getUsername());
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
