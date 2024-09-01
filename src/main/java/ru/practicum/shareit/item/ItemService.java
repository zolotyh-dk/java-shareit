package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto save(ItemDto dto, long ownerId);

    ItemDto update(ItemDto dto, long ownerId);

    ItemDto getById(long itemId);

    Collection<ItemDto> getAll(long ownerId);
}
