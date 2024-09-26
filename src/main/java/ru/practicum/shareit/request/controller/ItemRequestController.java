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
import java.util.List;

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
        final Collection<ItemRequestResponseWithItems> allRequests = requestService.getAllByRequestor(requestorId);
        log.info("В ответ на запрос GET /requests возвращаем все запросы вещией {}", allRequests);
        return allRequests;
    }
}
