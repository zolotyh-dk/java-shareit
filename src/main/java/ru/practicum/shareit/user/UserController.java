package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    final private UserService userService;

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос PUT /users на сохранение пользователя {}", userDto);
        final UserDto savedUserDto = userService.save(userDto);
        log.info("В ответ на запрос PUT /users возвращаем пользователя {}", savedUserDto);
        return savedUserDto;
    }

    @PatchMapping
    public UserDto update(@RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users на обновление пользователя {}", userDto);
        final UserDto updatedUserDto = userService.update(userDto);
        log.info("В ответ на запрос PATCH /users возвращаем пользователя {}", updatedUserDto);
        return updatedUserDto;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Получен запрос GET /users/{} на получение пользователя по id", id);
        final UserDto userDto = userService.getById(id);
        log.info("В ответ на запрос GET /users/{} возвращаем пользователя {}", id, userDto);
        return userDto;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Получен запрос DELETE /users/{} на удаление пользователя", id);
        userService.delete(id);
        log.info("Удалили пользователя по запросу DELETE /users/{}", id);
    }
}
