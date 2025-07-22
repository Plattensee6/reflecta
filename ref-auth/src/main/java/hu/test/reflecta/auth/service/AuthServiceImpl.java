package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.exception.AuthErrorMessages;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final AuthErrorMessages authErrorMessages;

    @Override
    public String getCurrentUsername() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            return user.getUsername();
        }
        throw new AccessDeniedException(authErrorMessages.getUserNotAuthenticated());
    }

    @Override
    public AppUser getCurrentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            return user;
        }
        throw new AccessDeniedException(authErrorMessages.getUserNotAuthenticated());
    }

    @Override
    public Long getCurrentUserId() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            return user.getId();
        }
        throw new AccessDeniedException(authErrorMessages.getUserNotAuthenticated());
    }

    @Override
    public Set<Role> getCurrentUserRoles() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            return user.getRoles();
        }
        return Set.of();
    }

    @Override
    public LoginResponse login(final LoginRequest request) throws Exception {
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
        return LoginResponse.builder()
                .token(token)
                .appUserId(appUser.getId())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Override
    public Boolean currentUserHasRole(final Role role) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUser user) {
            return user.hasRole(role);
        }
        return false;
    }
}
