package hu.test.reflecta.auth.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.auth")
public class AuthErrorMessages {
    private String userNotAuthenticated;
    private String accessDenied;
    private String jwtAuthFailed;
    private String accessibleNotImplemented;
}
