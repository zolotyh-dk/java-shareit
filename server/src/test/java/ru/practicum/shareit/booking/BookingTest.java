package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    @Test
    void testEqualsAndHashCode() {
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking1 = new Booking(1L, Instant.now(), Instant.now().plusSeconds(3600), item, booker, BookingStatus.APPROVED);
        Booking booking2 = new Booking(1L, Instant.now(), Instant.now().plusSeconds(3600), item, booker, BookingStatus.APPROVED);
        Booking booking3 = new Booking(2L, Instant.now(), Instant.now().plusSeconds(3600), item, booker, BookingStatus.APPROVED);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
    }
}
