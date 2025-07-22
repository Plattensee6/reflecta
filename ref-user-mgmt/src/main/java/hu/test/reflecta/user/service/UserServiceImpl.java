package hu.test.reflecta.user.service;

import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.repository.AppUserRepository;
import hu.test.reflecta.auth.repository.SecuredRepositoryProxy;
import hu.test.reflecta.auth.service.AuthService;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.mapper.UserMapper;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.data.repository.UserRepository;
import hu.test.reflecta.user.data.spec.UserSpecification;
import hu.test.reflecta.user.data.spec.UserSpecificationBuilder;
import hu.test.reflecta.user.exception.UserErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Default implementation of {@link UserService}.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserErrorMessage errorMessages;
    private final AppUserRepository appUserRepository;
    private final SecuredRepositoryProxy<User, Long> userRepository;

    public UserServiceImpl(final UserMapper mapper,
                           final UserErrorMessage userErrorMessage,
                           final AppUserRepository appUserRepository,
                           final UserRepository userRepository,
                           final AuthService authService) {
        this.mapper = mapper;
        this.errorMessages = userErrorMessage;
        this.appUserRepository = appUserRepository;
        this.userRepository = new SecuredRepositoryProxy<>(userRepository, userRepository, authService ,errorMessages);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse createUser(final UserRequest request) {
        log.debug("Creating user with email={}", request.getEmail());
        try {
            final User user = mapper.toEntity(request);
            final AppUser appUser = appUserRepository.getReferenceById(request.getAppUserId());
            user.setAppUser(appUser);
            User saved = userRepository.save(user);
            log.debug("User created with id={}", saved.getId());
            return mapper.toResponse(saved);
        } catch (Exception e) {
            log.error("Failed to create user with email={}", request.getEmail(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAllUsers(final Pageable pageable) {
        log.debug("Fetching all users with pageable={}", pageable);
        try {
            final Page<User> page = userRepository.getAll(pageable);
            if (CollectionUtils.isEmpty(page.toList())) {
                log.debug("No users found");
                throw new EntityNotFoundException(errorMessages.getUserNotFound());
            }
            log.debug("Found {} users", page.getTotalElements());
            return page.map(mapper::toResponse);
        } catch (Exception e) {
            log.error("Failed to fetch all users", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getById(final Long id) {
        log.debug("Fetching user by id={}", id);
        try {
            User user = userRepository.getById(id, true);
            log.debug("User found: id={}", id);
            return mapper.toResponse(user);
        } catch (Exception e) {
            log.error("Failed to fetch user by id={}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse updateUser(final Long id, final UserRequest request) {
        log.debug("Updating user id={}", id);
        try {
            User user = userRepository.getById(id, true);
            user.update(mapper.toEntity(request));
            User updated = userRepository.save(user);
            log.debug("User updated: id={}", id);
            return mapper.toResponse(updated);
        } catch (Exception e) {
            log.error("Failed to update user id={}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void deleteUser(final Long id) {
        log.debug("Deleting user id={}", id);
        try {
            User user = userRepository.getById(id, true);
            userRepository.delete(user, true);
            log.debug("User deleted: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete user id={}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean existsByEmail(final String email) {
        log.debug("Checking if user exists by email={}", email);
        try {
            Boolean exists = userRepository.existsBy(UserSpecification.hasEmail(email));
            log.debug("User exists by email={}: {}", email, exists);
            return exists;
        } catch (Exception e) {
            log.error("Failed to check if user exists by email={}", email, e);
            throw e;
        }
    }
}
