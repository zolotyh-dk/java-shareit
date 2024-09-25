package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

public interface ItemRequestService {
    ItemRequestResponse save(ItemRequestRequest request, long requestorId);
}
