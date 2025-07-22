package hu.test.reflecta.auth;

import hu.test.reflecta.auth.exception.AuthErrorMessages;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.jwt.JwtTokenProvider;
import hu.test.reflecta.auth.repository.AppUserRepository;
import hu.test.reflecta.auth.service.AuthServiceImpl;
import hu.test.reflecta.auth.service.AuthenticationException;
import hu.test.reflecta.auth.service.JwtService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppUserRepository userRepository;

    private AuthServiceImpl authService;

    private JwtService jwtService;
    @Mock
    private AuthenticationConfiguration authenticationConfiguration;
    @Mock
    private AuthErrorMessages authErrorMessages;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, authenticationConfiguration,
                jwtService, authErrorMessages);
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john", "password");
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash("hashed");
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findAppUserByusername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        when(jwtTokenProvider.getKey())
                .thenReturn(key);

        // Act
        LoginResponse result = authService.login(request);

        // Assert
        assertNotNull(result);
        assertThat(result.getToken()).isNotEmpty();
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("unknown", "password");

        when(userRepository.findAppUserByusername("unknown"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // Arrange
        LoginRequest request = new LoginRequest("john", "badpassword");
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash("hashed");
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findAppUserByusername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("badpassword", "hashed"))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void getCurrentUsername_ShouldReturnUsername() {
        // Arrange
        AppUser principal = new AppUser(1L, "john","", true,  LocalDateTime.now(), Set.of(Role.ROLE_USER));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        String username = authService.getCurrentUsername();

        // Assert
        assertThat(username).isEqualTo("john");
    }

    @Test
    void getCurrentUserId_ShouldReturnId() {
        // Arrange
        AppUser principal = new AppUser(42L, "john","", true,  LocalDateTime.now(), Set.of(Role.ROLE_USER));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Long id = authService.getCurrentUserId();

        // Assert
        assertThat(id).isEqualTo(42L);
    }

    @Test
    void getCurrentUserRoles_ShouldReturnRoles() {
        // Arrange
        AppUser principal = new AppUser(1L, "john","", true,  LocalDateTime.now(), Set.of(Role.ROLE_USER));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Set<Role> roles = authService.getCurrentUserRoles();

        // Assert
        assertThat(roles).containsExactlyInAnyOrder(Role.ROLE_ADMIN, Role.ROLE_USER);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}
