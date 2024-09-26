package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestResponseDto save(ItemRequestCreateDto request, long requestorId) {
        final User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + requestorId + " не найден"));
        final ItemRequest itemRequest = ItemRequestMapper.toEntity(request, requestor);
        log.debug("Преобразовали ItemRequestCreateDto -> {}", itemRequest);
        final ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        log.info("Сохранили в репозитории запрос вещи {}", savedItemRequest);
        return ItemRequestMapper.toResponseDto(savedItemRequest);
    }
}
