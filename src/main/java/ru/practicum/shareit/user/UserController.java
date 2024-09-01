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
    private final UserService userService;

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto dto) {
        log.info("Получен запрос POST /users на сохранение пользователя {}", dto);
        final UserDto savedDto = userService.save(dto);
        log.info("В ответ на запрос POST /users возвращаем пользователя {}", savedDto);
        return savedDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto dto, @PathVariable long id) {
        dto.setId(id);
        log.info("Получен запрос PATCH /users/{} на обновление пользователя {}", id, dto);
        final UserDto updatedDto = userService.update(dto);
        log.info("В ответ на запрос PATCH /users/{} возвращаем пользователя {}", id, updatedDto);
        return updatedDto;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Получен запрос GET /users/{} на получение пользователя по id", id);
        final UserDto dto = userService.getById(id);
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
