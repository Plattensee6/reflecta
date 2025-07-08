package hu.test.reflecta.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.cors")
@lombok.Value
public class CorsProperties {
    boolean enabled;
    List<String> allowedOrigins;
    List<String> allowedMethods;
    List<String> allowedHeaders;
    boolean allowedCredentials;
}