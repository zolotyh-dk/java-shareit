package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse book(@RequestBody BookingRequest request,
                                @RequestHeader(X_SHARER_USER_ID) long bookerId) {
        log.info("Получен запрос POST /bookings на бронирование аренды {}", request);
        final BookingResponse response = bookingService.book(request, bookerId);
        log.info("В ответ на запрос POST /bookings возвращаем бронирование {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateStatus(@PathVariable long bookingId,
                                        @RequestParam boolean approved,
                                        @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос на подтверждение аренды PATCH /bookings/{}?approved={}", bookingId, approved);
        final BookingResponse response = bookingService.updateStatus(bookingId, approved, ownerId);
        log.info("В ответ на запрос PATCH /bookings/{}?approved={} возвращаем бронирование {}", bookingId, approved, response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@PathVariable long bookingId, @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос на получение бронирования GET /bookings/{} от пользователя с id={}", bookingId, userId);
        final BookingResponse response = bookingService.getById(bookingId, userId);
        log.info("В ответ на запрос GET /bookings/{} возвращаем бронирование {}", bookingId, response);
        return response;
    }

    @GetMapping
    public Collection<BookingResponse> getByBookerAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestHeader(X_SHARER_USER_ID) long bookerId) {
        log.info("Получен запрос на получение бронирований GET /bookings?state={} для пользователя id={}", state, bookerId);
        final Collection<BookingResponse> bookings = bookingService.getByBookerAndState(state, bookerId);
        log.info("В ответ на запрос GET /bookings?state={} возвращаем бронирования {}", state, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingResponse> getByOwnerAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("Получен запрос на получение бронирований GET /bookings/owner?state={} для пользователя id={}", state, ownerId);
        final Collection<BookingResponse> bookings = bookingService.getByOwnerAndState(state, ownerId);
        log.info("В ответ на запрос GET /bookings?state={} возвращаем бронирования {}", state, bookings);
        return bookings;
    }
}