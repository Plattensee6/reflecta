package hu.test.reflecta.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class AuthErrorResponse {
    private HttpStatus code;
    private String message;
}
