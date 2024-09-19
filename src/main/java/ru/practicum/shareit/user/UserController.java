package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse save(@Valid @RequestBody UserRequest request) {
        log.info("Получен запрос POST /users на сохранение пользователя {}", request);
        final UserResponse response = userService.save(request);
        log.info("В ответ на запрос POST /users возвращаем пользователя {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public UserResponse update(@RequestBody UserRequest request, @PathVariable long id) {
        log.info("Получен запрос PATCH /users/{} на обновление пользователя {}", id, request);
        final UserResponse response = userService.update(request, id);
        log.info("В ответ на запрос PATCH /users/{} возвращаем пользователя {}", id, response);
        return response;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable long id) {
        log.info("Получен запрос GET /users/{} на получение пользователя по id", id);
        final UserResponse dto = userService.getById(id);
        log.info("В ответ на запрос GET /users/{} возвращаем пользователя {}", id, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Получен запрос DELETE /users/{} на удаление пользователя", id);
        userService.delete(id);
        log.info("Удалили пользователя по запросу DELETE /users/{}", id);
    }
}
