package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto toItemDto(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }

    public static User toUser(UserDto dto) {
        final User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}
