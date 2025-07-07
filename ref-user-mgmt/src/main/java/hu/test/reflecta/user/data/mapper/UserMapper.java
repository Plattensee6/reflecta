package hu.test.reflecta.user.data.mapper;


import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.data.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getName())
                .email(user.getEmail())
                .position(user.getPosition())
                .build();
    }

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.getFullName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .position(request.getPosition())
                .build();
    }

}
