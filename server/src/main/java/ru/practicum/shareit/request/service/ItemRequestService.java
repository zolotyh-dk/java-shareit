package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestWebRequest;
import ru.practicum.shareit.request.dto.ItemRequestWebResponse;
import ru.practicum.shareit.request.dto.ItemRequestWebResponseWithItems;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestWebResponse save(ItemRequestWebRequest request, long requestorId);

    Collection<ItemRequestWebResponseWithItems> getAllByRequestor(long requestorId);

    Collection<ItemRequestWebResponse> getAll(long userId);

    ItemRequestWebResponseWithItems getRequestById(long userId, long requestId);
}
