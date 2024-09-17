package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.ZoneId;

public class BookingMapper {
    public static BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                booking.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingRequest request, Item item, User booker, BookingStatus status) {
        final Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(request.getStart().atZone(ZoneId.systemDefault()).toInstant());
        booking.setEnd(request.getEnd().atZone(ZoneId.systemDefault()).toInstant());
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}
