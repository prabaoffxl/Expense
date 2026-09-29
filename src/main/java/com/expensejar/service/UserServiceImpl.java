package com.expensejar.service;

import com.expensejar.dto.UserRequestDTO;
import com.expensejar.dto.UserResponseDTO;
import com.expensejar.entity.User;
import com.expensejar.exception.DuplicateResourceException;
import com.expensejar.exception.ResourceNotFoundException;
import com.expensejar.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user after checking email uniqueness.
     */
    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("User with email '" + requestDTO.getEmail() + "' already exists");
        }

        User user = User.builder()
                .name(requestDTO.getName().trim())
                .email(requestDTO.getEmail().trim().toLowerCase())
                .build();

        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    /**
     * Retrieve user by ID or throw ResourceNotFoundException.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    /**
     * Retrieve all users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Update existing user information.
     */
    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String newEmail = requestDTO.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailAndIdNot(newEmail, id)) {
            throw new DuplicateResourceException("User with email '" + newEmail + "' already exists");
        }

        user.setName(requestDTO.getName().trim());
        user.setEmail(newEmail);

        User updated = userRepository.save(user);
        return mapToDTO(updated);
    }

    /**
     * Delete user by ID.
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
