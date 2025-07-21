package hu.test.reflecta.user.service;

import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.repository.AppUserRepository;
import hu.test.reflecta.auth.repository.SecuredRepositoryProxy;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.mapper.UserMapper;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.data.spec.UserSpecificationBuilder;
import hu.test.reflecta.user.exception.UserErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Default implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserErrorMessage errorMessages;
    private final AppUserRepository appUserRepository;
    private final SecuredRepositoryProxy<User, Long> userRepository;

    public UserServiceImpl(final UserMapper mapper,
                           final UserErrorMessage userErrorMessage,
                           final AppUserRepository appUserRepository,
                           final SecuredRepositoryProxy<User, Long> userRepository) {
        this.mapper = mapper;
        this.errorMessages = userErrorMessage;
        this.appUserRepository = appUserRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse createUser(final UserRequest request) {
        final User user = mapper.toEntity(request);
        final AppUser appUser = appUserRepository.getReferenceById(request.getAppUserId());
        user.setAppUser(appUser);
        User saved = userRepository.save(user);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAllUsers(final Pageable pageable) {
        final Page<User> page = userRepository.getAll(pageable);
        if (CollectionUtils.isEmpty(page.toList())) {
            throw new EntityNotFoundException(errorMessages.getUserNotFound());
        }
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getById(final Long id) {
        User user = userRepository.getById(id, true);
        return mapper.toResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateUser(final Long id, final UserRequest request) {
        User user = userRepository.getById(id, false);
        user.update(mapper.toEntity(request));
        User updated = userRepository.save(user);
        return mapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void deleteUser(final Long id) {
        final User user = userRepository.getById(id, true);
        userRepository.delete(user, true);
    }

    @Override
    public Boolean existsByEmail(final String email) {
        final Specification<User> spec = new UserSpecificationBuilder()
                .withEmail(email)
                .build();
        return userRepository.existsBy(spec);
    }
}
