package hu.test.reflecta.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private Long businessUserId;
    private long expiresIn;

}
