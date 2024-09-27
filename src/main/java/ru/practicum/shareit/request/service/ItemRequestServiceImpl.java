package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForItemRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponse save(ItemRequestCreate request, long requestorId) {
        final User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + requestorId + " не найден"));
        final ItemRequest itemRequest = ItemRequestMapper.toEntity(request, requestor);
        log.debug("Преобразовали ItemRequestCreateDto -> {}", itemRequest);
        final ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        log.info("Сохранили в репозитории запрос вещи {}", savedItemRequest);
        return ItemRequestMapper.toResponseDto(savedItemRequest);
    }

    @Override
    public Collection<ItemRequestResponseWithItems> getAllByRequestor(long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("Пользователь с id = " + requestorId + " не найден");
        }

        final Collection<ItemRequest> allRequests = requestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        if (allRequests.isEmpty()) {
            return Collections.emptyList();
        }

        final Set<Long> requestIds = allRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());

        final Collection<Item> items = itemRepository.findByRequestIdIn(requestIds);
        final Map<Long, List<Item>> requestToItemMap = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return allRequests.stream().map(request -> {
            List<Item> relatedItems = requestToItemMap.getOrDefault(request.getId(), Collections.emptyList());
            List<ItemForItemRequest> itemResponses = relatedItems.stream()
                    .map(ItemMapper::toItemForItemRequest)
                    .toList();
            return ItemRequestMapper.toResponseWithItems(request, itemResponses);
        }).toList();
    }

    @Override
    public Collection<ItemRequestResponse> getAll(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        final Collection<ItemRequest> itemRequests = requestRepository.findAllExcludingRequestor(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public ItemRequestResponseWithItems getRequestById(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        final ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id=" + requestId + " не найден"));
        final Collection<Item> relatedItems = itemRepository.findByRequestId(requestId);
        final List<ItemForItemRequest> itemDtos = relatedItems.stream()
                .map(ItemMapper::toItemForItemRequest)
                .toList();
        return ItemRequestMapper.toResponseWithItems(itemRequest, itemDtos);
    }
}
