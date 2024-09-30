package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestWebResponse;
import ru.practicum.shareit.request.dto.ItemRequestWebResponseWithItems;
import ru.practicum.shareit.request.dto.ItemRequestWebRequest;
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
    public ItemRequestWebResponse save(@RequestBody ItemRequestWebRequest request,
                                       @RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос POST /requests на сохранение запроса вещи {}", request);
        final ItemRequestWebResponse response = requestService.save(request, requestorId);
        log.info("В ответ на запрос POST /items возвращаем запрос вещи {}", response);
        return response;
    }

    @GetMapping
    public Collection<ItemRequestWebResponseWithItems> getAllByRequestor(@RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос GET /requests на получение запросов вещией от пользователя с id={}", requestorId);
        final Collection<ItemRequestWebResponseWithItems> requestsByUser = requestService.getAllByRequestor(requestorId);
        log.info("В ответ на запрос GET /requests возвращаем все запросы вещей от пользователя с id={} {}",
                requestorId, requestsByUser);
        return requestsByUser;
    }

    @GetMapping("/all")
    public Collection<ItemRequestWebResponse> getAll(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /requests/all на получение всех запросов вещией от пользователя с id={}", userId);
        final Collection<ItemRequestWebResponse> allRequests = requestService.getAll(userId);
        log.info("В ответ на запрос GET /requests/all возвращаем все запросы вещей {}", allRequests);
        return allRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWebResponseWithItems getRequestById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                          @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/{} от на получение запроса вещи от пользователя с id={}", requestId, userId);
        final ItemRequestWebResponseWithItems response = requestService.getRequestById(userId, requestId);
        log.info("В ответ на запрос GET /requests/{} возвращаем данные запроса: {}", requestId, response);
        return response;
    }
}