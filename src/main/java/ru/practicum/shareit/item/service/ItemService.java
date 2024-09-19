package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    ItemResponse save(ItemRequest request, long ownerId);

    ItemResponse update(ItemRequest request, long itemId, long ownerId);

    ItemDetailResponse getById(long itemId);

    Collection<ItemDetailResponse> getAll(long ownerId);

    Collection<ItemResponse> getByNameOrDescription(String text);

    CommentResponse addComment(long itemId, long userId, CommentRequest request);
}
