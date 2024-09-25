package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponse save(@Valid @RequestBody ItemRequestRequest request,
                                    @RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос POST /requests на сохранение запроса вещи {}", request);
        final ItemRequestResponse response = requestService.save(request, requestorId);
        log.info("В ответ на запрос POST /items возвращаем запрос вещи {}", response);
        return response;
    }
}
