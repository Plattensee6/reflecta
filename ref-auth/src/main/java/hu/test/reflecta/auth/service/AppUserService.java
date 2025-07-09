package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.dto.AppUserResponse;

public interface AppUserService {
    AppUserResponse create(AppUserRequest request);
}
