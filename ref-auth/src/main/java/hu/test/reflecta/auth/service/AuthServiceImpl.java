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

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;

    @Override
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getUsername();
        }
        return null;
    }

    @Override
    public JwtUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user;
        }
        return null;
    }


    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getId();
        }
        return null;
    }

    @Override
    public Set<Role> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getRoles();
        }
        return Set.of();
    }

    @Override
    public LoginResponse login(LoginRequest request) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        AppUser appUser = userRepository.findAppUserByusername(request.getUsername())
                .orElseThrow(EntityNotFoundException::new);
        String token = jwtService.generateToken(appUser);
        return LoginResponse.builder()
                .token(token)
                .businessUserId(appUser.getUserId())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Override
    public Boolean currentUserHasRole(Role role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.hasRole(role);
        }
        return false;
    }

    @Override
    public boolean isEligible(Participant meeting, Long currentUserId, boolean allowAdmin) {
        if (allowAdmin && currentUserHasRole(Role.ROLE_ADMIN)) {
            return true;
        }
        return meeting.isParticipant(currentUserId);
    }
}
