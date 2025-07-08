package hu.test.reflecta.app.auth.usecase;

import hu.test.reflecta.app.exception.AppErrorMessage;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.model.Position;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Service implementation that handles user registration workflows.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationFacadeImpl implements RegistrationFacade {
    private final UserService userService;
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppErrorMessage appErrorMessage;

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
        if (appUserRepository.findAppUserByusername(request.username()).isPresent()) {
            throw new IllegalArgumentException(appErrorMessage.getUsernameTaken());
        }
        if (userService.existsByEmail(request.email())) {
            throw new IllegalArgumentException(appErrorMessage.getEmailTaken());
        }
        final String password = request.password();
        final String passwordConf = request.passwordConfirm();
        if (StringUtils.equals(password, passwordConf)) {
            throw new IllegalArgumentException(appErrorMessage.getPwdNoMatch());
        }
        // User entity
        User user = new User();
        user.setEmail(request.email());
        user.setName(request.fullName());
        user.setDateOfBirth(request.dateOfBirth());
        user.setPosition(Position.EMPLOYEE); // admin users can register new admins
        UserRequest userRequest = UserRequest.builder()
                .email(request.email())
                .fullName(request.fullName())
                .dateOfBirth(request.dateOfBirth())
                .position(request.position())
                .build();

        // AppUser entity
        AppUser appUser = new AppUser();
        appUser.setUsername(request.username());
        appUser.setPasswordHash(passwordEncoder.encode(request.password()));
        appUser.setEnabled(true);
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setUserId(user.getId());
        appUser.setRoles(Set.of(Role.ROLE_READ)); // admins can grant additional permissions
        appUserRepository.save(appUser);
    }
}
