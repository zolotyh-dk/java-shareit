package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponse save(ItemRequest request, long ownerId) {
        final User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        final Item item = ItemMapper.requestToItem(request);
        item.setOwner(owner);
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

        // Если я верно понял задание, то вместе с Item возвращаются даты только текущего бронирования
        // Поэтому фильтрую по дате
        final Instant now = Instant.now();

        // Создаем мапу для быстрого доступа к текущим бронированиям по itemId
        final Map<Long, List<Booking>> bookingMap = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        final List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        final Map<Long, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream().map(item -> {
            List<Booking> currentBookings = bookingMap.get(item.getId());

            Instant startDate = currentBookings != null && !currentBookings.isEmpty()
                    ? currentBookings.get(0).getStart()
                    : null;

            Instant endDate = currentBookings != null && !currentBookings.isEmpty()
                    ? currentBookings.get(0).getEnd()
                    : null;

            return ItemMapper.toItemDetailResponse(item, startDate, endDate);
        }).toList();
    }

    @Override
    public Collection<ItemResponse> getByNameOrDescription(String text) {
        Collection<Item> items = itemRepository.searchByNameOrDescription(text);
        log.info("Получили из репозитория вещи доступные для аренды по запросу: {}. {}", text, items);
        return items.stream().map(ItemMapper::toItemResponse).toList();
    }

    @Override
    public CommentResponse addComment(long itemId, long userId, CommentRequest request) {
        final List<Booking> bookings = bookingRepository.findByBookerIdPastBookingsOrderByStartDesc(userId, Instant.now());
        if (bookings.stream().noneMatch(booking -> booking.getItem().getId() == itemId)) {
            throw new InvalidBookingDateException("У пользователя с id = " + userId + " нет завершенных аренд вещи с id = " + itemId);
        }
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
        final User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        final Comment comment = CommentMapper.toComment(request, item, author);
        final Comment savedComment = commentRepository.save(comment);
        log.info("Сохранили в репозитории комментарий {}", savedComment);
        return CommentMapper.toCommentResponse(comment);
    }
}
