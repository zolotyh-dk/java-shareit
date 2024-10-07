package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestWebRequest;
import ru.practicum.shareit.request.client.ItemRequestClient;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody ItemRequestWebRequest request,
                               @RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос POST /requests на сохранение запроса вещи {}", request);
        ResponseEntity<Object> response = requestClient.save(requestorId, request);
        log.info("В ответ на запрос POST /items возвращаем запрос вещи {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestor(@RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос GET /requests на получение запросов вещией от пользователя с id={}", requestorId);
        ResponseEntity<Object> requestsByUser = requestClient.getByRequestor(requestorId);
        log.info("В ответ на запрос GET /requests возвращаем все запросы вещей от пользователя с id={} {}",
                requestorId, requestsByUser);
        return requestsByUser;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /requests/all на получение всех запросов вещией от пользователя с id={}", userId);
        ResponseEntity<Object> allRequests = requestClient.getAll(userId);
        log.info("В ответ на запрос GET /requests/all возвращаем все запросы вещей {}", allRequests);
        return allRequests;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                       @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/{} от на получение запроса вещи от пользователя с id={}", requestId, userId);
        ResponseEntity<Object> response = requestClient.getById(userId, requestId);
        log.info("В ответ на запрос GET /requests/{} возвращаем данные запроса: {}", requestId, response);
        return response;
    }
}
