package hu.test.reflecta.user.data.dto;

import hu.test.reflecta.user.data.model.Position;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Position position;
}
