package hu.test.reflecta.auth.jwt;

import hu.test.reflecta.auth.exception.AuthErrorMessages;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter that processes JWT-based authentication for incoming HTTP requests.
 * <p>
 * This filter extracts the JWT token from the {@code Authorization} header, validates it,
 * and sets the authenticated user in the {@link SecurityContextHolder}.
 * </p>
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final String AUTH_HEADER = "authorization";
    private final String BEARER = "Bearer ";
    private final AuthErrorMessages authErrorMessages;


    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            final Long userId = jwtService.extractUserId(jwt);
            authenticate(username, userId, jwt, request);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            throw new JwtException(authErrorMessages.getJwtAuthFailed(), exception);
        }
    }

    /**
     * Attempts to authenticate the user with the provided JWT token and username.
     *
     * @param username the extracted username from the JWT
     * @param jwt      the JWT token
     * @param request  the HTTP request
     */
    private void authenticate(final String username,
                              final Long userId,
                              final String jwt,
                              final HttpServletRequest request) {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (username != null && existingAuth == null) {
            AppUser userDetails = (AppUser) userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }

    /**
     * Creates and sets the {@link UsernamePasswordAuthenticationToken} in the security context.
     *
     * @param userDetails the authenticated user's details
     * @param request     the HTTP request
     */
    private void setUpToken(final UserDetails userDetails, final HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authToken.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );
        SecurityContextHolder
                .getContext()
                .setAuthentication(authToken);
    }
}
