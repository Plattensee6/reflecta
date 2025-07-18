package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.model.JwtUserDetails;
import hu.test.reflecta.auth.model.Role;

import java.util.Set;

public interface AuthService {
    String getCurrentUsername();
    Long getCurrentUserId();
    Set<Role> getCurrentUserRoles();
    LoginResponse login(LoginRequest request) throws Exception;
    JwtUserDetails getCurrentUser();
    Boolean currentUserHasRole(Role role);
}
