package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestResponse save(ItemRequestCreate request, long requestorId);

    Collection<ItemRequestResponseWithItems> getAllByRequestor(long requestorId);

    Collection<ItemRequestResponse> getAll(long userId);

    ItemRequestResponseWithItems getRequestById(long userId, long requestId);
}
