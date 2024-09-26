package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

public interface ItemRequestService {
    ItemRequestResponseDto save(ItemRequestCreateDto request, long requestorId);
}
