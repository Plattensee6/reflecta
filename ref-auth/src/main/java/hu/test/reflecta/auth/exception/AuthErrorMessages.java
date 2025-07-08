package hu.test.reflecta.auth.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.meeting")
public class AuthenticationErrorMessages {
}
