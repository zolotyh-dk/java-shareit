package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@Validated(Create.class) @RequestBody UserRequest request) {
        log.info("Получен запрос POST /users на сохранение пользователя {}", request);
        ResponseEntity<Object> response = userClient.save(request);
        log.info("В ответ на запрос POST /users возвращаем пользователя {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Validated(Update.class) @RequestBody UserRequest request, @PathVariable long id) {
        log.info("Получен запрос PATCH /users/{} на обновление пользователя {}", id, request);
        ResponseEntity<Object> response = userClient.update(id, request);
        log.info("В ответ на запрос PATCH /users/{} возвращаем пользователя {}", id, response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        log.info("Получен запрос GET /users/{} на получение пользователя по id", id);
        ResponseEntity<Object> response = userClient.getById(id);
        log.info("В ответ на запрос GET /users/{} возвращаем пользователя {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Получен запрос DELETE /users/{} на удаление пользователя", id);
        userClient.delete(id);
        log.info("Удалили пользователя по запросу DELETE /users/{}", id);
    }
}
