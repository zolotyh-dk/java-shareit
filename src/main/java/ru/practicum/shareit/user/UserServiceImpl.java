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
    private final Map<Long, User> userRepository = new HashMap<>();
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
        userRepository.put(id, userToSave);
        emails.add(userToSave.getEmail());
        log.info("Сохранили в репозитории пользователя {}", userToSave);
        return UserMapper.toUserDto(userToSave);
    }

    @Override
    public UserDto update(UserDto userDto) {
        final User userToUpdate = UserMapper.toUser(userDto);
        log.debug("Преобразовали UserDto -> {}", userToUpdate);
        final long id = userToUpdate.getId();

        // Получаем текущего пользователя
        final User currentUser = userRepository.get(id);
        if (currentUser == null) {
            throw new NotFoundException("Пользователь c id: " + id + " не найден");
        }

        // Проверка уникальности email только если он изменился
        if (!currentUser.getEmail().equals(userToUpdate.getEmail())) {
            if (!isEmailUnique(userToUpdate.getEmail())) {
                log.warn("Email {} уже существует", userToUpdate.getEmail());
                throw new EmailAlreadyExistsException("Email: " + userToUpdate.getEmail() + " уже существует");
            }
            // Удаляем старый email из emails
            emails.remove(currentUser.getEmail());
        }

        userRepository.put(id, userToUpdate);
        emails.add(userToUpdate.getEmail());
        log.info("Обновили в репозитории пользователя {}", userToUpdate);
        return UserMapper.toUserDto(userToUpdate);
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
