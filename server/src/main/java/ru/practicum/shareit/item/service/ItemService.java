package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    ItemWebResponse save(ItemWebRequest request, long ownerId);

    ItemWebResponse update(ItemWebRequest request, long itemId, long ownerId);

    ItemDetailResponse getById(long itemId);

    Collection<ItemDetailResponse> getAll(long ownerId);

    Collection<ItemWebResponse> getByNameOrDescription(String text);

    CommentResponse addComment(long itemId, long userId, CommentRequest request);
}
