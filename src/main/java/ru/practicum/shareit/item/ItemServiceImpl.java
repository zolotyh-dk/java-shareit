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

import java.util.HashMap;
import java.util.Map;

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
}
