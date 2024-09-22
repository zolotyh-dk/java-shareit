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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
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
            List<Comment> comments = itemIdCommentsMap.get(itemId);

            List<Booking> bookings = itemIdBookingsMap.get(itemId);
            BookingPeriod lastBooking = null;
            BookingPeriod nextBooking = null;
            Instant now = Instant.now();

            if (bookings != null && !bookings.isEmpty()) {
                // Обходим все бронирования и находим lastBooking и nextBooking
                for (Booking booking : bookings) {
                    // Проверка для lastBooking (бронирование, которое завершилось в прошлом или идет сейчас)
                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                        lastBooking = BookingMapper.extractBookingPeriod(booking);
                        break; // Если мы нашли lastBooking в отсортированном ByStartDesc списке,
                               // то nextBooking тоже уже нашли или не найдем вовсе
                    }
                    // Проверка для nextBooking (бронирование, которое начинается в будущем)
                    if (booking.getStart().isAfter(now)) {
                        nextBooking = BookingMapper.extractBookingPeriod(booking);
                    }
                }
            }
            return ItemMapper.toItemDetailResponse(item, lastBooking, nextBooking, comments);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
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
