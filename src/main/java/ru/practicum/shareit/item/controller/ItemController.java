package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collection;
import java.util.Collections;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponse save(@Validated(Create.class) @RequestBody ItemRequest request,
                             @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос POST /items на сохранение вещи {}", request);
        final ItemResponse response = itemService.save(request, ownerId);
        log.info("В ответ на запрос POST /items возвращаем вещь {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@Validated(Update.class) @RequestBody ItemRequest request,
                               @PathVariable long itemId,
                               @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос PATCH /items/{} на обновление вещи {}", itemId, request);
        final ItemResponse response = itemService.update(request, itemId, ownerId);
        log.info("В ответ на запрос PATCH /items/{} возвращаем вещь {}", itemId, response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDetailResponse getById(@PathVariable long itemId) {
        log.info("Получен запрос GET /items/{} на получение вещи по id", itemId);
        final ItemDetailResponse response = itemService.getById(itemId);
        log.info("В ответ на запрос GET /items/{} возвращаем вещь {}", itemId, response);
        return response;
    }

    @GetMapping
    public Collection<ItemDetailResponse> getAll(@RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос GET /items на получение всех вещей от пользователя с id: {}", ownerId);
        final Collection<ItemDetailResponse> allItems = itemService.getAll(ownerId);
        log.info("В ответ на запрос GET /items возвращаем все вещи пользователя с id: {}. {}", ownerId, allItems);
        return allItems;
    }

    @GetMapping("/search")
    public Collection<ItemResponse> getByNameOrDescription(@RequestParam String text) {
        log.info("Получен запрос GET /items/search?text={} на получение вещей по названию или описанию", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        final Collection<ItemResponse> items = itemService.getByNameOrDescription(text);
        log.info("В ответ на запрос GET /items/search?text={} возвращаем вещи {}", text, items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@PathVariable long itemId,
                                      @RequestHeader(X_SHARER_USER_ID) long userId,
                                      @Valid @RequestBody CommentRequest request) {
        log.info("Получен запрос POST /items/{}/comment на добавление комментария", itemId);
        final CommentResponse response = itemService.addComment(itemId, userId, request);
        log.info("В ответ на запрос POST /items/{}/comment возвращаем комментарий {}", itemId, response);
        return response;
    }
}
