package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.check.RequireAccess;
import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.dto.AppUserResponse;
import hu.test.reflecta.auth.dto.AppUserRolesRequest;
import hu.test.reflecta.auth.mapper.AppUserMapper;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AuthService authService;
    private final PasswordEncoder encoder;

    @Override
    public AppUserResponse getById(final Long id) {
        return appUserMapper.toDto(appUserRepository.getReferenceById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public AppUserResponse create(final AppUserRequest request) {
        final AppUser appUser = appUserMapper.toEntity(
                request,
                authService.currentUserHasRole(Role.ROLE_ADMIN)
        );
        appUser.setPasswordHash(encoder.encode(request.getPasswordHash()));
        appUser.setEnabled(Boolean.TRUE);
        appUser.setCreatedAt(LocalDateTime.now());
        appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    @RequireAccess(allowAdmin = true)
    @Override
    public AppUserResponse addRoles(final AppUserRolesRequest request) {
        final Long appUserId = request.getAppUserId();
        final AppUser existing = appUserRepository.getReferenceById(appUserId);
        existing.addRoles(request.getNewRoles());
        return appUserMapper.toDto(existing);
    }

    @RequireAccess(allowAdmin = true)
    @Override
    public AppUserResponse revokeRoles(final AppUserRolesRequest request) {
        final Long appUserId = request.getAppUserId();
        final AppUser existing = appUserRepository.getReferenceById(appUserId);
        existing.addRoles(request.getNewRoles());
        return appUserMapper.toDto(existing);
    }
}
