package hu.test.reflecta.app.auth.usecase;

import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.repository.AppUserRepository;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.model.Position;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationFacadeImpl implements RegistrationFacade{
    private final UserService userService;
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(RegistrationRequest request) {
        if (appUserRepository.findAppUserByusername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userService.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already used");
        }
        final String password = request.password();
        final String passwordConf = request.passwordConfirm();
        if(StringUtils.equals(password, passwordConf)) {
            throw new IllegalArgumentException("The provided passwords do not match");
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
