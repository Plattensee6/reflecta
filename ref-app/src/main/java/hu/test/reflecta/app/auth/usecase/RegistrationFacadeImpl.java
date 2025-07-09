package hu.test.reflecta.app.auth.usecase;

import hu.test.reflecta.app.exception.AppErrorMessage;
import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.service.AppUserService;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Service implementation that handles user registration workflows.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationFacadeImpl implements RegistrationFacade {
    private final AppErrorMessage appErrorMessage;
    private final UserService userService;
    private final AppUserService appUserService;

    /**
     * Registers a new user account based on the provided registration request.
     * <p>
     * This method performs the following validations:
     * <ul>
     *     <li>Checks if the username is already in use.</li>
     *     <li>Checks if the email is already registered.</li>
     *     <li>Checks if the provided passwords match.</li>
     * </ul>
     * If validation passes, it creates and saves the corresponding {@link User} and {@link AppUser} entities.
     *
     * @param request the registration request containing user details and credentials
     * @throws IllegalArgumentException if the username or email is already used, or the passwords do not match
     */
    public void register(final RegistrationRequest request) {
        final String password = request.password();
        final String passwordConf = request.passwordConfirm();
        if (StringUtils.equals(password, passwordConf)) {
            throw new IllegalArgumentException(appErrorMessage.getPwdNoMatch());
        }
        final UserRequest userRequest = UserRequest.builder()
                .email(request.email())
                .fullName(request.fullName())
                .dateOfBirth(request.dateOfBirth())
                .position(request.position())
                .build();
        final UserResponse userResponse = userService.createUser(userRequest);
        final AppUserRequest appUserRequest = AppUserRequest.builder()
                .userId(userResponse.getId())
                .passwordHash(request.password())
                .username(request.username())
                .build();
        appUserService.create(appUserRequest);
    }
}
