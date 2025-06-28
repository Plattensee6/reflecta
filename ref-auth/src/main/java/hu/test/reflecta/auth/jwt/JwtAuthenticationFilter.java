package hu.test.reflecta.auth.jwt;

import hu.test.reflecta.auth.model.JwtUserDetails;
import hu.test.reflecta.auth.model.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            final String token = header.substring(7);

            if (jwtTokenProvider.validateToken(token)) {
                final Claims claims = jwtTokenProvider.getClaims(token);

                final String username = claims.getSubject();
                final Long userId = claims.get("userId", Long.class);
                final Set<Role> roles = claims.get("roles", Set.class);

                final JwtUserDetails principal = new JwtUserDetails(userId, username, roles);

                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
