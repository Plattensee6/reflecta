package hu.test.reflecta.auth.service;

import hu.test.reflecta.auth.dto.AppUserRequest;
import hu.test.reflecta.auth.dto.AppUserResponse;
import hu.test.reflecta.auth.mapper.AppUserMapper;
import hu.test.reflecta.auth.model.AppUser;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService{
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AuthService authService;

    @Transactional(readOnly = true)
    @Override
    public AppUserResponse create(final AppUserRequest request) {
        AppUser appUser = appUserMapper.toEntity(
                request,
                authService.currentUserHasRole(Role.ROLE_ADMIN)
        );
        appUser.setEnabled(Boolean.TRUE);
        appUser.setCreatedAt(LocalDateTime.now());
        appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }
}
