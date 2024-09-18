package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.ItemDetailResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public ItemResponse save(ItemRequest request, long ownerId) {
        final UserResponse owner = userService.getById(ownerId);
        final Item item = ItemMapper.requestToItem(request);
        item.setOwner(UserMapper.responseToUser(owner));
        log.debug("Преобразовали ItemDto -> {}", item);
        final Item savedItem = itemRepository.save(item);
        log.info("Сохранили в репозитории вещь {}", savedItem);
        return ItemMapper.toItemResponse(savedItem);
    }

    @Override
    public ItemResponse update(ItemRequest request, long itemId, long ownerId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена"));
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
        final Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemResponse(updatedItem);
    }

    @Override
    public ItemResponse getById(long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));
        log.info("Получили из репозитория вещь {}", item);
        return ItemMapper.toItemResponse(item);
    }

    @Override
    public Collection<ItemDetailResponse> getAll(long ownerId) {
        // Получаем все вещи пользователя
        final Collection<Item> items = itemRepository.findByOwnerId(ownerId);
        log.info("Получили из репозитория все вещи пользователя с id: {}. {}", ownerId, items);

        // Получаем все бронирования для этих вещей
        final List<Long> itemIds = items.stream().map(Item::getId).toList();
        final List<Booking> bookings = bookingRepository.findByItemIdIn(itemIds);

        // Получаем текущее время
        final Instant now = Instant.now();

        // Создаем карту для быстрого доступа к текущим бронированиям по itemId
        final Map<Long, List<Booking>> bookingMap = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items.stream().map(item -> {
            List<Booking> currentBookings = bookingMap.get(item.getId());

            Instant startDate = currentBookings != null && !currentBookings.isEmpty()
                    ? currentBookings.get(0).getStart()
                    : null;

            Instant endDate = currentBookings != null && !currentBookings.isEmpty()
                    ? currentBookings.get(0).getEnd()
                    : null;

            // Создаем ItemDetailResponse для каждой вещи
            return ItemMapper.toItemDetailResponse(item, startDate, endDate);
        }).toList();
    }

    @Override
    public Collection<ItemResponse> getByNameOrDescription(String text) {
        Collection<Item> items = itemRepository.searchByNameOrDescription(text);
        log.info("Получили из репозитория вещи доступные для аренды по запросу: {}. {}", text, items);
        return items.stream().map(ItemMapper::toItemResponse).toList();
    }
}
