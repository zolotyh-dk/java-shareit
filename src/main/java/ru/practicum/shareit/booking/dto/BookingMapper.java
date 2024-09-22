package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;

@UtilityClass
public class BookingMapper {
    public BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                booking.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                ItemMapper.toItemResponse(booking.getItem()),
                UserMapper.toUserResponse(booking.getBooker()),
                booking.getStatus()
        );
    }

    public Booking toBooking(BookingRequest request, Item item, User booker, BookingStatus status) {
        final Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(request.getStart().atZone(ZoneId.systemDefault()).toInstant());
        booking.setEnd(request.getEnd().atZone(ZoneId.systemDefault()).toInstant());
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }

    public BookingPeriod extractBookingPeriod(Booking booking) {
        return new BookingPeriod(
                booking.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                booking.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime()
                );
    }
}
