package hu.test.reflecta.service;

import hu.test.reflecta.auth.mapper.AppUserMapper;
import hu.test.reflecta.auth.repository.AppUserRepository;
import hu.test.reflecta.auth.repository.SecuredRepositoryProxy;
import hu.test.reflecta.auth.service.AppUserService;
import hu.test.reflecta.auth.service.AuthService;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.mapper.UserMapper;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.data.repository.UserRepository;
import hu.test.reflecta.user.exception.UserErrorMessage;
import hu.test.reflecta.user.service.UserService;
import hu.test.reflecta.user.service.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private SecuredRepositoryProxy<User, Long> securedRepositoryProxy;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userMapper, new UserErrorMessage(), appUserRepository,userRepository, authService);
    }

    @Test
    void getById_shouldReturnUserDto_whenUserExists() {
        // Arrange
        var entity = User.builder()
                .id(1L)
                .name("John")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        var dto = UserResponse.builder()
                .id(1L)
                .fullName("John")
                .build();
        when(userMapper.toResponse(entity)).thenReturn(dto);

        // Act
        var result = userService.getById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("john");
        verify(userRepository).findById(1L);
    }

    @Test
    void getById_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        // Arrange
        var request =  UserRequest.builder()
                .fullName("newuser")
                .build();

        var entity = User.builder()
                .name("newuser")
                .build();
        var savedEntity = User.builder()
                .id(5L)
                .name("newuser")
                .build();
        savedEntity.setId(5L);

        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(savedEntity);

        var dto = UserResponse.builder()
                .id(5L)
                .fullName("newuser")
                .build();
        when(userMapper.toResponse(savedEntity)).thenReturn(dto);

        // Act
        var result = userService.createUser(request);

        // Assert
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getFullName()).isEqualTo("newuser");
        verify(userRepository).save(entity);
    }

    @Test
    void update_shouldUpdateAndReturnDto() {
        // Arrange
        var request = UserRequest.builder()
                .fullName("updated")
                .build();

        var entity = User.builder()
                .id(3L)
                .name("old")
                .build();
        entity.setId(3L);

        when(userRepository.findById(3L)).thenReturn(Optional.of(entity));

        var updatedEntity = User.builder()
                .id(3L)
                .name("updated")
                .build();

        when(userRepository.save(entity)).thenReturn(updatedEntity);

        var dto = UserResponse.builder()
                .id(3L)
                .fullName("updated")
                .build();
        when(userMapper.toResponse(updatedEntity)).thenReturn(dto);

        // Act
        var result = userService.updateUser(3L, request);

        // Assert
        assertThat(result.getFullName()).isEqualTo("updated");
        verify(userRepository).save(entity);
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());
        var request = UserRequest.builder()
                .build();
        assertThatThrownBy(() -> userService.updateUser(42L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldCallRepository() {
        userService.deleteUser(10L);
        verify(userRepository).deleteById(10L);
    }
}
