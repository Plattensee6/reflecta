package hu.test.reflecta.meeting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "errors.meeting")
public class MeetingErrorMessages {
    private String meetingNotFound;
    private String userNotFound;
    private String finalizedAlreadyExists;
    private String invalidTimeRange;
    private String unauthorized;
    private String updateFinalized;
    private String deleteFinalized;
}
