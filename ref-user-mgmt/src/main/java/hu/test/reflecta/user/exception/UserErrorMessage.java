package hu.test.reflecta.user.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.user")
public class UserErrorMessage {
    private String userNotFound;

    public String getUserNotFound(final Long uid) {
        return userNotFound.concat(": " + uid);
    }
}
