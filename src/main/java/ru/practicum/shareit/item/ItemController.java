package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto save(@Valid @RequestBody ItemDto dto,
                        @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос POST /items на сохранение вещи {}", dto);
        final ItemDto savedDto = itemService.save(dto, ownerId);
        log.info("В ответ на запрос POST /items возвращаем вещь {}", savedDto);
        return savedDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto dto,
                          @PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос PATCH /items/{} на обновление вещи {}", itemId, dto);
        dto.setId(itemId);
        final ItemDto updatedDto = itemService.update(dto, ownerId);
        log.info("В ответ на запрос PATCH /items/{} возвращаем вещь {}", itemId, updatedDto);
        return updatedDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        log.info("Получен запрос GET /items/{} на получение вещи по id", itemId);
        final ItemDto dto = itemService.getById(itemId);
        log.info("В ответ на запрос GET /items/{} возвращаем вещь {}", itemId, dto);
        return dto;
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос GET /items на получение всех вещей от пользователя с id: {}", ownerId);
        final Collection<ItemDto> allDtos = itemService.getAll(ownerId);
        log.info("В ответ на запрос GET /items возвращаем все вещи пользователя с id: {}. {}", ownerId, allDtos);
        return allDtos;
    }

    @GetMapping("/search")
    public Collection<ItemDto> getByNameOrDescription(@RequestParam String text) {
        log.info("Получен запрос GET /items/search?text={} на получение вещей по названию или описанию", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        final Collection<ItemDto> searchedDtos = itemService.getByNameOrDescription(text);
        log.info("В ответ на запрос GET /items/search?text={} возвращаем вещи {}", text, searchedDtos);
        return searchedDtos;
    }
}
