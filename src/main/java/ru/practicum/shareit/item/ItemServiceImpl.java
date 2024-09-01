package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
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
    public ItemDto save(ItemDto dto, long ownerId) {
        final UserDto ownerDto = userService.getById(ownerId);
        final Item itemToSave = ItemMapper.toItem(dto);
        itemToSave.setOwner(UserMapper.toUser(ownerDto));
        log.debug("Преобразовали ItemDto -> {}", itemToSave);
        final long id = ++lastId;
        itemToSave.setId(id);
        items.put(id, itemToSave);
        log.info("Сохранили в репозитории вещь {}", itemToSave);
        return ItemMapper.toItemDto(itemToSave);
    }

    @Override
    public ItemDto update(ItemDto dto, long ownerId) {
        final long id = dto.getId();
        final Item currentItem = items.get(id);

        if (currentItem == null) {
            throw new NotFoundException("Вещь с id: " + id + " не найдена");
        }

        if (currentItem.getOwner().getId() != ownerId) {
            throw new UnauthorizedAccessException("У пользователя с id: " + ownerId + " нет прав на обновление этой вещи.");
        }

        currentItem.setName(dto.getName());
        currentItem.setDescription(dto.getDescription());
        currentItem.setAvailable(dto.getAvailable());
        return ItemMapper.toItemDto(currentItem);
    }

    @Override
    public ItemDto getById(long itemId) {
        final Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id: " + itemId + " не найдена.");
        }
        log.info("Получили из репозитория вещь {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAll(long ownerId) {
        final List<ItemDto> allDtos = items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto)
                .toList();
        log.info("Получили из репозитория все вещи пользователя с id: {}. {}", ownerId, allDtos);
        return allDtos;
    }

    @Override
    public Collection<ItemDto> getByNameOrDescription(String text) {
        final String lowerCaseText = text.toLowerCase();

        final Collection<ItemDto> searchedDtos = items.values().stream()
                .filter(item -> Optional.ofNullable(item.getAvailable()).orElse(false) &&
                        (Optional.ofNullable(item.getName()).orElse("").toLowerCase().contains(lowerCaseText) ||
                                Optional.ofNullable(item.getDescription()).orElse("").toLowerCase().contains(lowerCaseText)))
                .map(ItemMapper::toItemDto)
                .toList();

        log.info("Получили из репозитория вещи доступные для аренды по запросу: {}. {}", text, searchedDtos);
        return searchedDtos;
    }
}
