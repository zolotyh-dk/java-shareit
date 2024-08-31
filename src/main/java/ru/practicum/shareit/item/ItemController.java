package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    final private ItemService itemService;

    @PostMapping
    public ItemDto save(@Valid @RequestBody ItemDto dto,
                        @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос POST /items на сохранение вещи {}", dto);
        final ItemDto savedDto = itemService.save(dto, ownerId);
        log.info("В ответ на запрос POST /items возвращаем вещь {}", savedDto);
        return savedDto;
    }


}
