package hu.test.reflecta.meeting.config;

import hu.test.reflecta.auth.exception.SecurityErrorMessages;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.meeting")
public class MeetingErrorMessages implements SecurityErrorMessages {
    private String meetingNotFound;
    private String userNotFound;
    private String finalizedAlreadyExists;
    private String invalidTimeRange;
    private String unauthorized;
    private String updateFinalized;
    private String deleteFinalized;

    @Override
    public String entityNotFound() {
        return meetingNotFound;
    }

    @Override
    public String unauthorized() {
        return unauthorized;
    }
}
