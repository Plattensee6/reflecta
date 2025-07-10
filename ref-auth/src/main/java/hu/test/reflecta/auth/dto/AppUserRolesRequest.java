package hu.test.reflecta.auth.dto;

import hu.test.reflecta.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@AllArgsConstructor
@Builder
public class AppUserRolesRequest {
    Set<Role> newRoles;
    Long appUserId;
}
