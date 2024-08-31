package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {

    ItemDto save(ItemDto dto, long ownerId);

    ItemDto update(ItemDto dto, long ownerId);
}
