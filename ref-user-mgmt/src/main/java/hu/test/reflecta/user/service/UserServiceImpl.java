package hu.test.reflecta.user.service;

import hu.test.reflecta.auth.check.RequireAccess;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.mapper.UserMapper;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.data.repository.UserRepository;
import hu.test.reflecta.user.exception.UserErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link UserService}.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final UserErrorMessage userErrorMessage;

    @Transactional(readOnly = true)
    @RequireAccess(allowAdmin = true)
    @Override
    public UserResponse createUser(final UserRequest request) {
        final User user = mapper.toEntity(request);
        User saved = userRepository.save(user);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @RequireAccess(allowAdmin = true)
    @Override
    public UserResponse getById(final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(userErrorMessage.getUserNotFound(id)));
        return mapper.toResponse(user);
    }

    @Transactional
    @RequireAccess
    @Override
    public UserResponse updateUser(final Long id, final UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(userErrorMessage.getUserNotFound(id)));
        user.update(mapper.toEntity(request));
        User updated = userRepository.save(user);
        return mapper.toResponse(updated);
    }

    @Transactional
    @RequireAccess(allowAdmin = true)
    @Override
    public void deleteUser(final Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(userErrorMessage.getUserNotFound(id));
        }
        userRepository.deleteById(id);
    }

    @Override
    public Boolean existsByEmail(final String email) {
        return userRepository.existsByEmail(email);
    }
}
