package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto getById(long id);

    void delete(long id);
}
