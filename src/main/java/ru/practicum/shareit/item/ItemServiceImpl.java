package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
}
