package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public User requestToUser(UserRequest request) {
        final User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }
}
