package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingResponse book(BookingRequest request, long bookerId);

    BookingResponse updateStatus(long bookingId, boolean approved, long ownerId);

    BookingResponse getById(long bookingId, long userId);

    Collection<BookingResponse> getByBookerAndState(BookingState state, long bookerId);

    Collection<BookingResponse> getByOwnerAndState(BookingState state, long ownerId);
}
