package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.ItemNotAvailable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.Instant;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;


    @Override
    public BookingResponse book(BookingRequest request, long bookerId) {
        if (!request.getStart().isBefore(request.getEnd())) {
            throw new InvalidBookingDateException("Дата начала бронирования = " + request.getStart() +
                                                  " должна быть раньше даты окончания = " + request.getEnd());
        }
        final UserResponse booker = userService.getById(bookerId);
        final ItemResponse item = itemService.getById(request.getItemId());
        if (!item.getAvailable()) {
            throw new ItemNotAvailable("Вещь c id=" + item.getId() + " не доступна для бронирования");
        }
        final Booking booking = BookingMapper.toBooking(
                request,
                ItemMapper.responseToItem(item),
                UserMapper.responseToUser(booker),
                BookingStatus.WAITING
        );
        log.debug("Преобразовали BookingRequest -> {}", booking);
        bookingRepository.save(booking);
        log.info("Сохранили в репозитории бронирование {}", booking);
        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse updateStatus(long bookingId, boolean approved, long ownerId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new UnauthorizedAccessException("Пользователь с id=" + ownerId + " не является владельцем вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Обновили статус бронирования {}", booking);
        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse getById(long bookingId, long userId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (ownerId != userId && bookerId != userId) {
            throw new UnauthorizedAccessException("Пользователь с id=" + userId + " не является владельцем вещи и не бронировал её");
        }
        log.info("Получили из репозитория бронирование {}", booking);
        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    public Collection<BookingResponse> getByBookerAndState(BookingState state, long bookerId) {
        userService.getById(bookerId);
        Collection<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrentBookingsOrderByStartDesc(bookerId, Instant.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdPastBookingsOrderByStartDesc(bookerId, Instant.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdFutureBookingsOrderByStartDesc(bookerId, Instant.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
                break;
                case ALL:
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
        }
        log.info("Получили из репозитория бронирования {}", bookings);
        return bookings.stream().map(BookingMapper::toBookingResponse).toList();
    }

    @Override
    public Collection<BookingResponse> getByOwnerAndState(BookingState state, long ownerId) {
        userService.getById(ownerId);
        Collection<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByOwnerIdCurrentBookingsOrderByStartDesc(ownerId, Instant.now());
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerIdPastBookingsOrderByStartDesc(ownerId, Instant.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdFutureBookingsOrderByStartDesc(ownerId, Instant.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllByOwnerIdOrderByStartDesc(ownerId);
                break;
        }
        log.info("Получили из репозитория бронирования {}", bookings);
        return bookings.stream().map(BookingMapper::toBookingResponse).toList();
    }


}
