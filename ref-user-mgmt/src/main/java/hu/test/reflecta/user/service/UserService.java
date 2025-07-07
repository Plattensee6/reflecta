package hu.test.reflecta.user.service;


import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse createUser(UserRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getById(Long id);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    Boolean existsByEmail(String email);
}
