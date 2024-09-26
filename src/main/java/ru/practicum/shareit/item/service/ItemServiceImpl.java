package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemResponse save(ItemRequest request, long ownerId) {
        final User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        final ru.practicum.shareit.request.model.ItemRequest itemRequest = requestRepository.findById(request.getRequestId())
                        .orElseThrow(() -> new NotFoundException("Запрос вещи с id=" + request.getRequestId() + " не найден"));
        final Item item = ItemMapper.toItem(request, itemRequest);
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
        final String newName = request.getName();
        if (newName != null && !newName.isBlank()) {
            item.setName(newName);
        }
        final String newDescription = request.getDescription();
        if (newDescription != null && !newDescription.isBlank()) {
            item.setDescription(newDescription);
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }
        final Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemResponse(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDetailResponse getById(long itemId) {
        // Получаем вещь по идентификатору
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));
        log.info("Получили из репозитория вещь {}", item);

        // Получаем комментарии для этой вещи
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        log.info("Получили комментарии для вещи с id {}: {}", itemId, comments);

        // Получаем все бронирования для этой вещи
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(itemId);
        final Instant now = Instant.now();
        BookingPeriod lastBooking = null;
        BookingPeriod nextBooking = null;

        for (Booking booking : bookings) {
            if ((booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))) {
                lastBooking = BookingMapper.extractBookingPeriod(booking);
                break;
            }
            if (booking.getStart().isAfter(now)) {
                nextBooking = BookingMapper.extractBookingPeriod(booking);
            }
        }

        // Возвращаем ItemDetailResponse с комментариями и информацией о бронированиях
        return ItemMapper.toItemDetailResponse(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDetailResponse> getAll(long ownerId) {
        // Получаем все вещи пользователя
        final Collection<Item> items = itemRepository.findByOwnerId(ownerId);
        log.info("Получили из репозитория все вещи пользователя с id: {}. {}", ownerId, items);

        final List<Long> itemIds = items.stream().map(Item::getId).toList();

        // Получаем все комменты для этих вещей
        final Map<Long, List<Comment>> itemIdCommentsMap = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        // Получаем все бронирования вещей этого владельца
        final Map<Long, List<Booking>> itemIdBookingsMap = bookingRepository.findAllByOwnerIdOrderByStartDesc(ownerId)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items.stream().map(item -> {
            long itemId = item.getId();
            List<Comment> comments = itemIdCommentsMap.getOrDefault(itemId, List.of());
            List<Booking> bookings = itemIdBookingsMap.getOrDefault(itemId, List.of());
            BookingPeriod lastBooking = null;
            BookingPeriod nextBooking = null;
            Instant now = Instant.now();
            for (Booking booking : bookings) {
                if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                    lastBooking = BookingMapper.extractBookingPeriod(booking);
                    break;
                }
                if (booking.getStart().isAfter(now)) {
                    nextBooking = BookingMapper.extractBookingPeriod(booking);
                }
            }
            return ItemMapper.toItemDetailResponse(item, lastBooking, nextBooking, comments);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemResponse> getByNameOrDescription(String text) {
        if (text.isBlank()) {
            log.info("Получили в запросе пустую строку, возвращаем пустой список");
            return Collections.emptyList();
        }
        final Collection<Item> items = itemRepository.searchByNameOrDescription(text);
        log.info("Получили из репозитория вещи доступные для аренды по запросу: {}. {}", text, items);
        return items.stream().map(ItemMapper::toItemResponse).toList();
    }

    @Override
    public CommentResponse addComment(long itemId, long userId, CommentRequest request) {
        boolean hasPastBookings = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, Instant.now());
        if (!hasPastBookings) {
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
