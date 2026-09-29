package com.expensejar.service;

import com.expensejar.dto.UserRequestDTO;
import com.expensejar.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    /**
     * Create a new user.
     */
    UserResponseDTO createUser(UserRequestDTO requestDTO);

    /**
     * Get user by ID.
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Get all users.
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Update an existing user.
     */
    UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO);

    /**
     * Delete user by ID.
     */
    void deleteUser(Long id);
}
