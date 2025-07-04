package hu.test.reflecta.auth;

import hu.test.reflecta.datasource.appuser.model.AppUser;
import hu.test.reflecta.datasource.appuser.model.Role;
import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.jwt.JwtTokenProvider;
import hu.test.reflecta.auth.model.JwtUserDetails;
import hu.test.reflecta.datasource.appuser.repository.AppUserRepository;
import hu.test.reflecta.auth.service.AuthServiceImpl;
import hu.test.reflecta.auth.service.AuthenticationException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppUserRepository userRepository;

    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(jwtTokenProvider, passwordEncoder, userRepository);
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() {
        // Arrange
        LoginRequest request = new LoginRequest("john", "password");
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash("hashed");
        user.setRoles(Set.of(Role.USER));

        when(userRepository.findAppUserByusername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        when(jwtTokenProvider.getKey())
                .thenReturn(key);

        // Act
        Optional<LoginResponse> result = authService.login(request);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isNotEmpty();
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
        user.setRoles(Set.of(Role.USER));

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
        JwtUserDetails principal = new JwtUserDetails(1L, "john", Set.of(Role.USER));
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
        JwtUserDetails principal = new JwtUserDetails(42L, "john", Set.of(Role.USER));
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
        JwtUserDetails principal = new JwtUserDetails(1L, "john", Set.of(Role.USER));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Set<Role> roles = authService.getCurrentUserRoles();

        // Assert
        assertThat(roles).containsExactlyInAnyOrder(Role.ADMIN, Role.USER);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}
