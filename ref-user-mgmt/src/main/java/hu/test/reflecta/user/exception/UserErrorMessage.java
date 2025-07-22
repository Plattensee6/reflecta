package hu.test.reflecta.user.exception;

import hu.test.reflecta.auth.exception.SecurityErrorMessages;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.user")
public class UserErrorMessage implements SecurityErrorMessages {
    private String userNotFound;
    private String unauthorized;

    public String getUserNotFound(final Long uid) {
        return userNotFound.concat(": " + uid);
    }

    public String getNotAuthorized(final Long uid) {
        return unauthorized.concat(": " + uid);
    }

    @Override
    public String entityNotFound() {
        return userNotFound;
    }

    @Override
    public String unauthorized() {
        return unauthorized;
    }
}
