package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private long lastId;

    @Override
    public ItemResponse save(ItemRequest request, long ownerId) {
        final UserResponse owner = userService.getById(ownerId);
        final Item item = ItemMapper.toItem(request);
        item.setOwner(UserMapper.responseToUser(owner));
        log.debug("Преобразовали ItemDto -> {}", item);
        final long id = ++lastId;
        item.setId(id);
        items.put(id, item);
        log.info("Сохранили в репозитории вещь {}", item);
        return ItemMapper.toItemResponse(item);
    }

    @Override
    public ItemResponse update(ItemRequest request, long itemId, long ownerId) {
        final Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id: " + itemId + " не найдена");
        }
        if (item.getOwner().getId() != ownerId) {
            throw new UnauthorizedAccessException("У пользователя с id: " + ownerId + " нет прав на обновление этой вещи.");
        }
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }
        return ItemMapper.toItemResponse(item);
    }

    @Override
    public ItemResponse getById(long itemId) {
        final Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id: " + itemId + " не найдена.");
        }
        log.info("Получили из репозитория вещь {}", item);
        return ItemMapper.toItemResponse(item);
    }

    @Override
    public Collection<ItemResponse> getAll(long ownerId) {
        final List<ItemResponse> allItems = items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemResponse)
                .toList();
        log.info("Получили из репозитория все вещи пользователя с id: {}. {}", ownerId, allItems);
        return allItems;
    }

    @Override
    public Collection<ItemResponse> getByNameOrDescription(String text) {
        final String lowerCaseText = text.toLowerCase();

        final Collection<ItemResponse> searchedItems = items.values().stream()
                .filter(item -> Optional.ofNullable(item.getAvailable()).orElse(false) &&
                        (Optional.ofNullable(item.getName()).orElse("").toLowerCase().contains(lowerCaseText) ||
                                Optional.ofNullable(item.getDescription()).orElse("").toLowerCase().contains(lowerCaseText)))
                .map(ItemMapper::toItemResponse)
                .toList();

        log.info("Получили из репозитория вещи доступные для аренды по запросу: {}. {}", text, searchedItems);
        return searchedItems;
    }
}
