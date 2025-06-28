package hu.test.reflecta.auth.service;

import hu.test.reflecta.datasource.appuser.model.Role;
import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;

import java.util.Optional;
import java.util.Set;

public interface AuthService {
    String getCurrentUsername();
    Long getCurrentUserId();
    Set<Role> getCurrentUserRoles();
    Optional<LoginResponse> login(LoginRequest request);
}
