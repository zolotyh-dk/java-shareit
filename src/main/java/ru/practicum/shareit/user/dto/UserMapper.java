package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public static User requestToUser(UserRequest request) {
        final User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }

    public static User responseToUser(UserResponse response) {
        final User user = new User();
        user.setId(response.getId());
        user.setName(response.getName());
        user.setEmail(response.getEmail());
        return user;
    }
}
