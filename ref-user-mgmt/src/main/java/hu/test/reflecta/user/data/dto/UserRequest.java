package hu.test.reflecta.user.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.test.reflecta.user.data.model.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    String fullName;
    @JsonProperty("email")
    String email;
    Position position;
    LocalDateTime dateOfBirth;
    Long appUserId;
}