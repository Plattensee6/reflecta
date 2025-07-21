package hu.test.reflecta.user.service;


import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing {@link User} entities.
 */
public interface UserService {
    /**
     * Creates a new user.
     *
     * @param request the {@link UserRequest} containing user data
     * @return the created {@link UserResponse}
     */
    UserResponse createUser(UserRequest request);

    /**
     * Retrieves all users.
     *
     * @return a list of {@link UserResponse} DTOs
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the {@link UserResponse} of the found user
     */
    UserResponse getById(Long id);

    /**
     * Updates an existing user.
     *
     * @param id      the ID of the user to update
     * @param request the {@link UserRequest} with updated data
     * @return the updated {@link UserResponse}
     */
    UserResponse updateUser(Long id, UserRequest request);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email to check
     * @return {@code true} if a user exists with the given email; otherwise, {@code false}
     */
    Boolean existsByEmail(String email);

}
