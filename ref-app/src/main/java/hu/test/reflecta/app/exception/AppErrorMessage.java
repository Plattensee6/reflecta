package hu.test.reflecta.app.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.app")
public class AppErrorMessage {
    private String usernameTaken;
    private String emailTaken;
    private String pwdNoMatch;
}
