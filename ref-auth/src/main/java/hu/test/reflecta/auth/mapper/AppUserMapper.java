package hu.test.reflecta.auth.mapper;

import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.dto.AppUserResponse;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class AppUserMapper {
    public AppUserResponse toDto(final AppUser appUser) {
        return AppUserResponse.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .build();
    }

    public AppUser toEntity(final AppUserRequest appUserRequest, final boolean hasAuthority) {
        final Set<Role> roles = hasAuthority?
                appUserRequest.getRoles()
                : new HashSet<>(Set.of(Role.ROLE_USER, Role.ROLE_READ));
        return AppUser.builder()
                .roles(roles)
                .username(appUserRequest.getUsername())
                .passwordHash(appUserRequest.getPasswordHash())
                .build();
    }
}
