package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.dto.AppUserResponse;
import hu.test.reflecta.auth.dto.AppUserRolesRequest;

public interface AppUserService {
    AppUserResponse create(AppUserRequest request);
    AppUserResponse addRoles(final AppUserRolesRequest request);
    AppUserResponse revokeRoles(final AppUserRolesRequest request);
}
