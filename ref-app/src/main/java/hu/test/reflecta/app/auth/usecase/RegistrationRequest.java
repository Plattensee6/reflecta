package hu.test.reflecta.app.auth.usecase;

import hu.test.reflecta.user.data.model.Position;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record RegistrationRequest (
        String fullName,
        String email,
        Position position,
        LocalDateTime dateOfBirth,
        String username,
        String password,
        String passwordConfirm,
        Set<String> roles
){
}
