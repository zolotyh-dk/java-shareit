package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponse save(@Valid @RequestBody ItemRequestCreate request,
                                    @RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос POST /requests на сохранение запроса вещи {}", request);
        final ItemRequestResponse response = requestService.save(request, requestorId);
        log.info("В ответ на запрос POST /items возвращаем запрос вещи {}", response);
        return response;
    }

    @GetMapping
    public Collection<ItemRequestResponseWithItems> getAllByRequestor(@RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос GET /requests на получение запросов вещией от пользователя с id={}", requestorId);
        final Collection<ItemRequestResponseWithItems> requestsByUser = requestService.getAllByRequestor(requestorId);
        log.info("В ответ на запрос GET /requests возвращаем все запросы вещей от пользователя с id={} {}",
                requestorId, requestsByUser);
        return requestsByUser;
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponse> getAll(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /requests/all на получение всех запросов вещией от пользователя с id={}", userId);
        final Collection<ItemRequestResponse> allRequests = requestService.getAll(userId);
        log.info("В ответ на запрос GET /requests/all возвращаем все запросы вещей {}", allRequests);
        return allRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseWithItems getRequestById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                       @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/{} от на получение запроса вещи от пользователя с id={}", requestId, userId);
        final ItemRequestResponseWithItems response = requestService.getRequestById(userId, requestId);
        log.info("В ответ на запрос GET /requests/{} возвращаем данные запроса: {}", requestId, response);
        return response;
    }
}
