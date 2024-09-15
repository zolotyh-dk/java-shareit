package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse save(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email: {} уже существует", request.getEmail());
            throw new EmailAlreadyExistsException("Email: " + request.getEmail() + " уже существует");
        }
        final User userToSave = UserMapper.requestToUser(request);
        log.debug("Преобразовали UserDto -> {}", userToSave);
        userRepository.save(userToSave);
        log.info("Сохранили в репозитории пользователя {}", userToSave);
        return UserMapper.toUserResponse(userToSave);
    }

    @Override
    public UserResponse update(UserRequest request, long id) {
        final User currentUser = userRepository.getReferenceById(id);
        if (request.getName() != null) {
            currentUser.setName(request.getName());
        }
        if (request.getEmail() != null && !currentUser.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Email: {} уже существует", request.getEmail());
                throw new EmailAlreadyExistsException("Email: " + request.getEmail() + " уже существует");
            }
            currentUser.setEmail(request.getEmail());
        }
        log.info("Обновили в репозитории пользователя {}", currentUser);
        return UserMapper.toUserResponse(currentUser);
    }

    @Override
    public UserResponse getById(long id) {
        final User user = userRepository.getReferenceById(id);
        log.info("Получили из репозитория пользователя {}", user);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
        log.info("Удалили из репозитория пользователя c id = {}", id);
    }
}
