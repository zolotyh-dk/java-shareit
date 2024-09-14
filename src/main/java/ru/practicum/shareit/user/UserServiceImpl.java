package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
//    private final Map<Long, User> users = new HashMap<>();
//    private final Set<String> emails = new HashSet<>();
//    private long lastId;
    private final UserRepository userRepository;

    @Override
    public UserResponse save(UserRequest request) {
//        if (!isEmailUnique(request.getEmail())) {
//            log.warn("Email {} уже существует", request.getEmail());
//            throw new EmailAlreadyExistsException("Email: " + request.getEmail() + " уже существует");
//        }

        final User userToSave = UserMapper.requestToUser(request);
        log.debug("Преобразовали UserDto -> {}", userToSave);
//        final long id = ++lastId;
//        userToSave.setId(id);
//        users.put(id, userToSave);
//        emails.add(userToSave.getEmail());
        userRepository.save(userToSave);
        log.info("Сохранили в репозитории пользователя {}", userToSave);
        return UserMapper.toUserResponse(userToSave);
    }

    @Override
    public UserResponse update(UserRequest request, long id) {
        final User currentUser = userRepository.getReferenceById(id);
        currentUser.setName(request.getName());
        currentUser.setEmail(request.getEmail());
//        if (currentUser == null) {
//            throw new NotFoundException("Пользователь c id: " + id + " не найден");
//        }
//        if (request.getName() != null) {
//            currentUser.setName(request.getName());
//        }
//        if (request.getEmail() != null && !currentUser.getEmail().equals(request.getEmail())) {
//            if (!isEmailUnique(request.getEmail())) {
//                log.warn("Email {} уже существует", request.getEmail());
//                throw new EmailAlreadyExistsException("Email: " + request.getEmail() + " уже существует");
//            }
//            emails.remove(currentUser.getEmail());
//            emails.add(request.getEmail());
//            currentUser.setEmail(request.getEmail());
//        }
        log.info("Обновили в репозитории пользователя {}", currentUser);
        return UserMapper.toUserResponse(currentUser);
    }

    @Override
    public UserResponse getById(long id) {
        final User user = userRepository.getReferenceById(id);
//        if (user == null) {
//            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
//        }
        log.info("Получили из репозитория пользователя {}", user);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
//        if (deletedUser != null) {
//            emails.remove(deletedUser.getEmail());
            log.info("Удалили из репозитория пользователя c id = {}", id);
//        }
    }

//    private boolean isEmailUnique(String email) {
//        log.debug("Проверяем что такого email: {} еще нет в хранилище {}", email, emails);
//        return !emails.contains(email);
//    }
}
