package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.dto.AppUserResponse;
import hu.test.reflecta.auth.dto.AppUserRolesRequest;
import hu.test.reflecta.auth.exception.AuthErrorMessages;
import hu.test.reflecta.auth.mapper.AppUserMapper;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AuthService authService;
    private final PasswordEncoder encoder;
    private final AuthErrorMessages errorMessages;

    @Override
    public AppUserResponse getById(final Long id) {
        log.debug("Fetching AppUser by id={}", id);
        try {
            if (!authService.getCurrentUserId().equals(id)) {
                log.error("Access denied for AppUser id={}", id);
                throw new AccessDeniedException(errorMessages.getAccessDenied());
            }
            AppUserResponse response = appUserMapper.toDto(appUserRepository.getReferenceById(id));
            log.debug("AppUser found: id={}", id);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch AppUser by id={}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public AppUserResponse create(final AppUserRequest request) {
        log.debug("Creating AppUser with username={}", request.getUsername());
        try {
            final AppUser appUser = appUserMapper.toEntity(
                    request,
                    authService.currentUserHasRole(Role.ROLE_ADMIN)
            );
            appUser.setPasswordHash(encoder.encode(request.getPasswordHash()));
            appUser.setEnabled(Boolean.TRUE);
            appUser.setCreatedAt(LocalDateTime.now());
            appUserRepository.save(appUser);
            log.debug("AppUser created with username={}", appUser.getUsername());
            return appUserMapper.toDto(appUser);
        } catch (Exception e) {
            log.error("Failed to create AppUser with username={}", request.getUsername(), e);
            throw e;
        }
    }

    @Override
    public AppUserResponse addRoles(final AppUserRolesRequest request) {
        log.debug("Adding roles to AppUser id={}", request.getAppUserId());
        try {
            final Long appUserId = request.getAppUserId();
            final AppUser existing = appUserRepository.getReferenceById(appUserId);
            existing.addRoles(request.getNewRoles());
            AppUserResponse response = appUserMapper.toDto(existing);
            log.debug("Roles added to AppUser id={}", appUserId);
            return response;
        } catch (Exception e) {
            log.error("Failed to add roles to AppUser id={}", request.getAppUserId(), e);
            throw e;
        }
    }

    @Override
    public AppUserResponse revokeRoles(final AppUserRolesRequest request) {
        log.debug("Revoking roles from AppUser id={}", request.getAppUserId());
        try {
            final Long appUserId = request.getAppUserId();
            final AppUser existing = appUserRepository.getReferenceById(appUserId);
            existing.addRoles(request.getNewRoles());
            AppUserResponse response = appUserMapper.toDto(existing);
            log.debug("Roles revoked from AppUser id={}", appUserId);
            return response;
        } catch (Exception e) {
            log.error("Failed to revoke roles from AppUser id={}", request.getAppUserId(), e);
            throw e;
        }
    }
}
