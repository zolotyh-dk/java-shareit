package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDetailResponse;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.Collection;

public interface ItemService {

    ItemResponse save(ItemRequest request, long ownerId);

    ItemResponse update(ItemRequest request, long itemId, long ownerId);

    ItemResponse getById(long itemId);

    Collection<ItemDetailResponse> getAll(long ownerId);

    Collection<ItemResponse> getByNameOrDescription(String text);
}
