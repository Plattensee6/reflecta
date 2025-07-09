package hu.test.reflecta.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import hu.test.reflecta.auth.model.Role;
import java.util.HashSet;
import java.util.Set;

@Value
@AllArgsConstructor
@Builder
public class AppUserRequest {
    Long id;
    String username;
    String passwordHash;
    Boolean enabled = true;
    Set<Role> roles = new HashSet<>();
    Long userId;
}
