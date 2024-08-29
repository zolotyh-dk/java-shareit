package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private Map<Long, User> userRepository = new HashMap<>();
    private long lastId;

    @Override
    public UserDto save(UserDto userDto) {
        final User userToSave = UserMapper.toUser(userDto);
        final long id = ++lastId;
        userToSave.setId(id);
        final User savedUser = userRepository.put(id, userToSave);
        log.info("Сохранили в репозитории пользователя {}", savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto update(UserDto userDto) {
        final User userToUpdate = UserMapper.toUser(userDto);
        final long id = userToUpdate.getId();
        final User updatedUser = userRepository.put(id, userToUpdate);
        log.info("Обновили в репозитории пользователя {}", updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getById(long id) {
        final User user = userRepository.get(id);
        log.info("Получили из репозитория пользователя {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        final User deletedUser = userRepository.remove(id);
        log.info("Удалили из репозитория пользователя {}", deletedUser);
    }
}
