package hu.test.reflecta.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class responsible for JWT token generation, validation, and extraction of claims.
 */
@Slf4j
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String extractUsername(final String token) {
        log.debug("Extracting username from JWT token");
        try {
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Failed to extract username from JWT token", e);
            throw e;
        }
    }

    /**
     * Extracts the userId (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the userId contained in the token
     */
    public Long extractUserId(String token) {
        log.debug("Extracting userId from JWT token");
        try {
            Long userId = extractClaim(token, claims -> {
                Object userIdClaim = claims.get("userId");
                if (userIdClaim instanceof Number number) {
                    return number.longValue();
                }
                throw new JwtException("Invalid or missing userId claim");
            });
            log.debug("Extracted userId: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Failed to extract userId from JWT token", e);
            throw e;
        }
    }

    /**
     * Extracts a custom claim from the JWT token.
     *
     * @param token          the JWT token
     * @param claimsResolver a function to extract a specific claim from the {@link Claims} object
     * @param <T>            the type of the claim to extract
     * @return the extracted claim value
     */
    public <T> T extractClaim(final String token,
                              final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token containing the user details.
     *
     * @param userDetails the user details to include in the token
     * @return the generated JWT token
     */
    public String generateToken(final UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional custom claims.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details to include in the token
     * @return the generated JWT token
     */
    public String generateToken(final Map<String, Object> extraClaims,
                                final UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Returns the configured expiration time for tokens.
     *
     * @return the expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Builds a JWT token with the specified claims and expiration.
     *
     * @param extraClaims additional claims to include
     * @param userDetails the user details
     * @param expiration  the expiration time in milliseconds
     * @return the generated JWT token
     */
    private String buildToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails,
            final long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates whether the given JWT token belongs to the specified user and is not expired.
     *
     * @param token       the JWT token
     * @param userDetails the user details to validate against
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public boolean isTokenValid(final String token,
                                final UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks whether the JWT token has expired.
     *
     * @param token the JWT token
     * @return {@code true} if the token is expired; {@code false} otherwise
     */
    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the {@link Claims} object containing all claims
     */
    private Claims extractAllClaims(final String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Returns the cryptographic signing key used to sign JWT tokens.
     *
     * @return the {@link Key} used for signing tokens
     */
    private Key getSignInKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
