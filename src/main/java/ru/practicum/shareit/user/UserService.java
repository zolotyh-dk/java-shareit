package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    UserResponse save(UserRequest request);

    UserResponse update(UserRequest request, long id);

    UserResponse getById(long id);

    void delete(long id);
}
