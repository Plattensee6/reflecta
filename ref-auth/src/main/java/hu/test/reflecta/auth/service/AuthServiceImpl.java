package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.check.Participant;
import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.JwtUserDetails;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    @Override
    public String getCurrentUsername() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getUsername();
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public JwtUserDetails getCurrentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user;
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Long getCurrentUserId() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getId();
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Role> getCurrentUserRoles() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getRoles();
        }
        return Set.of();
    }

    @Transactional
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
        final String token = jwtService.generateToken(appUser);
        return LoginResponse.builder()
                .token(token)
                .businessUserId(appUser.getUserId())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean currentUserHasRole(final Role role) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.hasRole(role);
        }
        return false;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEligible(final Participant meeting,
                              final Long currentUserId,
                              final boolean allowAdmin) {
        if (allowAdmin && currentUserHasRole(Role.ROLE_ADMIN)) {
            return true;
        }
        return meeting.isParticipant(currentUserId);
    }
}
