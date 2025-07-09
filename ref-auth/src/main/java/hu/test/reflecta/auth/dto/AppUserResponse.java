package hu.test.reflecta.auth.dto;

import hu.test.reflecta.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
@AllArgsConstructor
@Builder
public class AppUserResponse {
    Long id;
    String username;
    String passwordHash;
    Boolean enabled = true;
    Set<Role> roles = new HashSet<>();
    Long userId;
}
