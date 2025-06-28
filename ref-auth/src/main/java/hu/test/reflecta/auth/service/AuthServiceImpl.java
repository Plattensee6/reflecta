package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.jwt.JwtTokenProvider;
import hu.test.reflecta.auth.model.JwtUserDetails;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    @Override
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails user) {
            return user.getUsername();
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
    public Optional<LoginResponse> login(LoginRequest request) {
        final AppUser appUser = userRepository.findAppUserByusername(request.getUsername())
                .orElseThrow(EntityNotFoundException::new);
        if (!passwordEncoder.matches(request.getPassword(), appUser.getPasswordHash())) {
            throw new AuthenticationException();
        }
        final String token = Jwts.builder()
                .setSubject(appUser.getUsername())
                .claim("userId", appUser.getId())
                .claim("roles", appUser.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(jwtTokenProvider.getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Optional.of(new LoginResponse(token));
    }
}
