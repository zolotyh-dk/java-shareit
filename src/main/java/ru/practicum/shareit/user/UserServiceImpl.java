package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();

    //Храним email'ы в отдельном Set для проверки уникальности за О(1)
    private final Set<String> emails = new HashSet<>();

    private long lastId;

    @Override
    public UserDto save(UserDto userDto) {
        if (!isEmailUnique(userDto.getEmail())) {
            log.warn("Email {} уже существует", userDto.getEmail());
            throw new EmailAlreadyExistsException("Email: " + userDto.getEmail() + " уже существует");
        }

        final User userToSave = UserMapper.toUser(userDto);
        log.debug("Преобразовали UserDto -> {}", userToSave);
        final long id = ++lastId;
        userToSave.setId(id);
        users.put(id, userToSave);
        emails.add(userToSave.getEmail());
        log.info("Сохранили в репозитории пользователя {}", userToSave);
        return UserMapper.toUserDto(userToSave);
    }

    @Override
    public UserDto update(UserDto userDto) {
        final long id = userDto.getId();

        final User currentUser = users.get(id);
        if (currentUser == null) {
            throw new NotFoundException("Пользователь c id: " + id + " не найден");
        }

        if (userDto.getName() != null) {
            currentUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !currentUser.getEmail().equals(userDto.getEmail())) {
            if (!isEmailUnique(userDto.getEmail())) {
                log.warn("Email {} уже существует", userDto.getEmail());
                throw new EmailAlreadyExistsException("Email: " + userDto.getEmail() + " уже существует");
            }
            emails.remove(currentUser.getEmail());
            emails.add(userDto.getEmail());
            currentUser.setEmail(userDto.getEmail());
        }

        log.info("Обновили в репозитории пользователя {}", currentUser);
        return UserMapper.toUserDto(currentUser);
    }

    @Override
    public UserDto getById(long id) {
        final User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
        log.info("Получили из репозитория пользователя {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        final User deletedUser = users.remove(id);
        if (deletedUser != null) {
            emails.remove(deletedUser.getEmail());
            log.info("Удалили из репозитория пользователя {}", deletedUser);
        }
    }

    private boolean isEmailUnique(String email) {
        log.debug("Проверяем что такого email: {} еще нет в хранилище {}", email, emails);
        return !emails.contains(email);
    }
}
