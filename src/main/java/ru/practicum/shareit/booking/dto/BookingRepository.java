package ru.practicum.shareit.booking.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = ?1
                AND b.start <= ?2
                AND b.end >= ?2
            ORDER BY b.start DESC
                """)
    List<Booking> findCurrentBookings(long bookerId, Instant now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = ?1
            AND b.end < ?2
        ORDER BY b.start DESC
        """)
    List<Booking> findPastBookings(long bookerId, Instant now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = ?1
            AND b.start > ?2
        ORDER BY b.start DESC
        """)
    List<Booking> findFutureBookings(long bookerId, Instant now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);
}
