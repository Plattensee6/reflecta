package hu.test.reflecta.user.data.mapper;


import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between {@link User} entities and their DTO representations.
 */
@Component
public class UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserResponse} DTO.
     *
     * @param user the user entity to convert
     * @return the corresponding {@link UserResponse}
     */
    public UserResponse toResponse(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getName())
                .email(user.getEmail())
                .position(user.getPosition())
                .build();
    }

    /**
     * Converts a {@link UserRequest} DTO to a {@link User} entity.
     *
     * @param request the request DTO containing user data
     * @return the corresponding {@link User} entity
     */
    public User toEntity(final UserRequest request) {
        return User.builder()
                .name(request.getFullName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .position(request.getPosition())
                .build();
    }

}
