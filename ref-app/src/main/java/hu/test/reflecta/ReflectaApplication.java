package hu.test.reflecta;

import hu.test.reflecta.meeting.config.MeetingErrorMessages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties({
        MeetingErrorMessages.class
})
@EntityScan
public class ReflectaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReflectaApplication.class, args);
    }
}
