package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.exception.AuthErrorMessages;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final AuthErrorMessages authErrorMessages;

    @Override
    public String getCurrentUsername() {
        log.debug("Fetching current username");
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            log.debug("Current username: {}", user.getUsername());
            return user.getUsername();
        }
        log.error("User not authenticated");
        throw new AccessDeniedException(authErrorMessages.getUserNotAuthenticated());
    }

    @Override
    public AppUser getCurrentUser() {
        log.debug("Fetching current AppUser");
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            log.debug("Current AppUser id: {}", user.getId());
            return user;
        }
        log.error("User not authenticated");
        throw new AccessDeniedException(authErrorMessages.getUserNotAuthenticated());
    }

    @Override
    public Long getCurrentUserId() {
        log.debug("Fetching current user id");
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            log.debug("Current user id: {}", user.getId());
            return user.getId();
        }
        log.error("User not authenticated");
        throw new AccessDeniedException(authErrorMessages.getUserNotAuthenticated());
    }

    @Override
    public Set<Role> getCurrentUserRoles() {
        log.debug("Fetching current user roles");
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            log.debug("Current user roles: {}", user.getRoles());
            return user.getRoles();
        }
        return Set.of();
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(final LoginRequest request) throws Exception {
        log.debug("Attempting login for username={}", request.getUsername());
        try {
            final AuthenticationManager authenticationManager = authenticationConfiguration
                    .getAuthenticationManager();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            final AppUser appUser = userRepository.findAppUserByusername(request.getUsername())
                    .orElseThrow(EntityNotFoundException::new);
            final Map<String, Object> extraClaims = Map.of("userId", appUser.getId());
            final String token = jwtService.generateToken(extraClaims, appUser);
            log.debug("Login successful for username={}", request.getUsername());
            return LoginResponse.builder()
                    .token(token)
                    .appUserId(appUser.getId())
                    .expiresIn(jwtService.getExpirationTime())
                    .build();
        } catch (Exception e) {
            log.error("Login failed for username={}", request.getUsername(), e);
            throw e;
        }
    }

    @Override
    public Boolean currentUserHasRole(final Role role) {
        log.debug("Checking if current user has role={}", role);
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            boolean hasRole = user.hasRole(role);
            log.debug("Current user has role {}: {}", role, hasRole);
            return hasRole;
        }
        return false;
    }
}
