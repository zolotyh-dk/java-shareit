package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collections;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@Validated(Create.class) @RequestBody ItemRequest request,
                                           @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос POST /items на сохранение вещи {}", request);
        ResponseEntity<Object> response = itemClient.saveItem(ownerId, request);
        log.info("В ответ на запрос POST /items возвращаем вещь {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class) @RequestBody ItemRequest request,
                                             @PathVariable long itemId,
                                             @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос PATCH /items/{} на обновление вещи {}", itemId, request);
        ResponseEntity<Object> response = itemClient.updateItem(ownerId, itemId, request);
        log.info("В ответ на запрос PATCH /items/{} возвращаем вещь {}", itemId, response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId) {
        log.info("Получен запрос GET /items/{} на получение вещи по id", itemId);
        ResponseEntity<Object> response = itemClient.getItemById(itemId);
        log.info("В ответ на запрос GET /items/{} возвращаем вещь {}", itemId, response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос GET /items на получение всех вещей от пользователя с id: {}", ownerId);
        ResponseEntity<Object> allItems = itemClient.getAllItems(ownerId);
        log.info("В ответ на запрос GET /items возвращаем все вещи пользователя с id: {}. {}", ownerId, allItems);
        return allItems;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByNameOrDescription(@RequestParam String text) {
        log.info("Получен запрос GET /items/search?text={} на получение вещей по названию или описанию", text);
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        ResponseEntity<Object> items = itemClient.getItemByNameOrDescription(text);
        log.info("В ответ на запрос GET /items/search?text={} возвращаем вещи {}", text, items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                      @RequestHeader(X_SHARER_USER_ID) long userId,
                                      @Valid @RequestBody CommentRequest request) {
        log.info("Получен запрос POST /items/{}/comment на добавление комментария", itemId);
        ResponseEntity<Object> response = itemClient.saveComment(userId, itemId, request);
        log.info("В ответ на запрос POST /items/{}/comment возвращаем комментарий {}", itemId, response);
        return response;
    }
}
