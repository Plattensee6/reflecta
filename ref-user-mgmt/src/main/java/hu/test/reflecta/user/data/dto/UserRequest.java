package hu.test.reflecta.user.data.dto;

import hu.test.reflecta.user.data.model.Position;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserRequest {
    private Long id;
    private String fullName;
    private String email;
    private Position position;
    private LocalDateTime dateOfBirth;
}
